package com.springboot.cozyheavenstay.service;

import com.springboot.cozyheavenstay.dto.request.BookingQuoteRequestDto;
import com.springboot.cozyheavenstay.dto.request.BookingRoomRequestDto;
import com.springboot.cozyheavenstay.dto.response.*;
import com.springboot.cozyheavenstay.enums.BookingStatus;
import com.springboot.cozyheavenstay.enums.HotelStatus;
import com.springboot.cozyheavenstay.enums.RoomStatus;
import com.springboot.cozyheavenstay.enums.VerificationStatus;
import com.springboot.cozyheavenstay.exception.InvalidCallException;
import com.springboot.cozyheavenstay.exception.OwnerShipException;
import com.springboot.cozyheavenstay.exception.ResourceNotFoundException;
import com.springboot.cozyheavenstay.mapper.BookingMapper;
import com.springboot.cozyheavenstay.model.*;
import com.springboot.cozyheavenstay.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class BookingService {

    private final RoomRepository roomRepository;
    private final BookingRoomsRepository bookingRoomsRepository;
    private final CustomerRepository customerRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;

    public BookingQuoteResponseDto getQuote(BookingQuoteRequestDto dto){
        //validate dates
        LocalDate today=LocalDate.now();

        if(dto.checkIn().isBefore(today))
            throw new InvalidCallException("Check-in cannot be in the past");

        if(!dto.checkOut().isAfter(dto.checkIn()))
            throw new InvalidCallException("Check-out must be after check-In");


        long nights = ChronoUnit.DAYS.between(dto.checkIn(),dto.checkOut());

        Set<Long> selectedRoomIds = new HashSet<>();
        List<BookingRoomQuoteDto> roomQuotes= new ArrayList<>();

        Long hotelId = null;
        double totalAmount = 0;

        for(BookingRoomRequestDto selection: dto.rooms()){
            //prevent selecting the same room twice
            if(!selectedRoomIds.add(selection.roomId()))
                throw new InvalidCallException("The same room cannot be selected twice");

            //fetch room
            Rooms room=roomRepository.findById(selection.roomId()).orElseThrow(()->
                    new ResourceNotFoundException("Room Not Found : "+selection.roomId()));

            Hotels hotel=room.getHotel();

            if(hotelId==null){
                hotelId=hotel.getId();
            } else if (!hotelId.equals(hotel.getId())) {
                throw new InvalidCallException("Select rooms from same hotel");
            }

            // check hotel status and room status
            if(hotel.getVerificationStatus()!= VerificationStatus.APPROVED || hotel.getHotelStatus()!= HotelStatus.ACTIVE||room.getRoomStatus()!= RoomStatus.ACTIVE)
                throw new InvalidCallException("Selected room is not available for bookings");

            //check guest capacity
            int adults= selection.adults();
            int children = selection.children();

            if((long)adults+children>room.getMaxOccupancy())
                throw new InvalidCallException("Guest count exceeds capacity for room "+room.getRoomNumber());

            //check existing reservations
            long overlappingBookings= bookingRoomsRepository.countOverlappingBookings(room.getId(), List.of(BookingStatus.PENDING,BookingStatus.CONFIRMED),dto.checkIn(),dto.checkOut());

            if(overlappingBookings>0)
                throw new InvalidCallException("Room "+room.getRoomNumber()+" is unavailable for selected dates");

            //find the number included in the baseFare
            int includedGuests = switch (room.getBedType()){
                case SINGLE -> 1;
                case DOUBLE -> 2;
                case KING -> 4;
            };

            //fill included place with adults first
            int includedAdults = Math.min(adults,includedGuests);
            int remainingPlaces = includedGuests-includedAdults;
            int includeChildren = Math.min(children,remainingPlaces);

            int extraAdults= adults-includedAdults;
            int extraChildren = children-includeChildren;

            //calculate price
            double baseFare = room.getBaseFare();
            double extraCharges= roundMoney(baseFare*0.40*extraAdults + baseFare*0.20*extraChildren);

            double pricePerNight = roundMoney(baseFare+extraCharges);
            double subTotal = roundMoney(pricePerNight*nights);

            totalAmount= roundMoney(totalAmount+subTotal);

            roomQuotes.add(new BookingRoomQuoteDto(
                    room.getId(),
                    room.getRoomNumber(),
                    adults,
                    children,
                    baseFare,
                    extraCharges,
                    pricePerNight,
                    subTotal
            ));
        }

        return new BookingQuoteResponseDto(
                hotelId,
                dto.checkIn(),
                dto.checkOut(),
                nights,
                roomQuotes,
                totalAmount
        );
    }

    private double roundMoney(double amount){
        return Math.round(amount*100.0)/100.0;
    }

    @Transactional
    public BookingResponseDto addBooking(BookingQuoteRequestDto dto, String customerUsername){
        //find logged in user
        Customer customer=customerRepository.getByUsername(customerUsername);

        if(customer==null)
            throw new ResourceNotFoundException("Customer profile not found");

        //validate selection and calculate current price
        BookingQuoteResponseDto quote= getQuote(dto);

        //prepare booking
        Instant now =Instant.now();
        Bookings booking= new Bookings();
        booking.setCustomer(customer);
        booking.setCheckIn(dto.checkIn());
        booking.setCheckOut(dto.checkOut());
        booking.setBookedAt(now);
        booking.setExpiresAt(now.plusSeconds((15*60)));
        booking.setBookingStatus(BookingStatus.PENDING);
        booking.setTotalAmount(quote.totalAmount());

        //save booking to get its ID
        booking=bookingRepository.save(booking);

        //save each selected room
        for(BookingRoomQuoteDto roomQuote: quote.rooms()){
            Rooms room = roomRepository.findById(roomQuote.roomId()).orElseThrow(()->
                    new ResourceNotFoundException("Room not found"));

            BookingRooms bookingRoom = new BookingRooms();
            bookingRoom.setBooking(booking);
            bookingRoom.setRoom(room);
            bookingRoom.setAdults(roomQuote.adults());
            bookingRoom.setChildren(roomQuote.children());
            bookingRoom.setPricePerNight(roomQuote.pricePerNight());
            bookingRoom.setSubtotal(roomQuote.subTotal());
            bookingRoomsRepository.save(bookingRoom);
        }

        //return booking details
        return new BookingResponseDto(booking.getId(),
                booking.getCheckIn(),
                booking.getCheckOut(),
                booking.getTotalAmount(),
                booking.getBookingStatus(),
                booking.getExpiresAt()
        );
    }


    public List<BookingResponseDto> getMyBookings(String customerUsername,Integer page,Integer size){
        //validate pagination
        if(page<0||size<1||size>100)
            throw new InvalidCallException("Page must be 0 and above, and size must be between 1 and 100");

        Pageable pageable= PageRequest.of(page,size);

        //fetch customer bookings
        return bookingRepository.getBookingsByCustomerUsername(customerUsername,pageable);
    }

    public BookingDetailsResponseDto getBookingDetails(Long bookingId, String customerUsername){
        //Find booking
        Bookings booking= bookingRepository.findById(bookingId).orElseThrow(()->
                new ResourceNotFoundException("Booking Not Found"));

        //check ownerShip

        if(!booking.getCustomer().getUser().getUsername().equals(customerUsername))
            throw new OwnerShipException("You can view only your booking");

        //find rooms linked to this booking
        List<BookingRooms> bookingRooms= bookingRoomsRepository.getRoomsByBookingId(bookingId);
        if(bookingRooms.isEmpty())
            throw new InvalidCallException("No rooms found for this booking");

        //every room in a booking belongs to the same hotel
        Hotels hotel = bookingRooms.get(0).getRoom().getHotel();

        //convert room details to DTOs
        List<BookingRoomDetailsDto> rooms = bookingRooms.stream().map(
                BookingMapper::convertBookingRoomToDto
        ).toList();

        //find payment, if exist
        Payments payment= paymentRepository.getPaymentsByBookingId(bookingId).orElse(null);

        PaymentResponseDto paymentDto = null;
        if(payment!=null){
            paymentDto= new PaymentResponseDto(
                    payment.getId(),
                    booking.getId(),
                    payment.getAmount(),
                    payment.getPaymentStatus(),
                    booking.getBookingStatus(),
                    payment.getPaidAt()
            );
        }

        //calculate number of nights
        long nights = ChronoUnit.DAYS.between(booking.getCheckIn(),booking.getCheckOut());

        //return complete booking details
        return new BookingDetailsResponseDto(
                booking.getId(),
                hotel.getId(),
                hotel.getHotelName(),
                hotel.getAddress(),
                booking.getCheckIn(),
                booking.getCheckOut(),
                nights,
                booking.getTotalAmount(),
                booking.getBookingStatus(),
                booking.getBookedAt(),
                booking.getExpiresAt(),
                rooms,
                paymentDto
        );
    }

    @Transactional
    public BookingResponseDto cancelBooking(Long bookingId,String customerUsername){
        //find bookings
        Bookings booking = bookingRepository.findById(bookingId).orElseThrow(()->
                new ResourceNotFoundException("Booking Not Found"));

        //check ownership
        if(!booking.getCustomer().getUser().getUsername().equals(customerUsername))
            throw new OwnerShipException("You can cancel only your bookings");

        //allow only pending or confirmed bookings
        if(booking.getBookingStatus()!=BookingStatus.PENDING&&booking.getBookingStatus()!=BookingStatus.CONFIRMED)
            throw new InvalidCallException("Only pending or confirmed bookings can be cancelled");

        //check cancellation deadline
        LocalDate today= LocalDate.now();
        if(!booking.getCheckIn().isAfter(today))
            throw new InvalidCallException("Cancellation is allowed only before the check-in date");

        //cancel booking
        booking.setBookingStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        //return updated booking
        return BookingMapper.convertEntityToDto(booking);
    }
}
