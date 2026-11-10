package com.springboot.cozyheavenstay.service;

import com.springboot.cozyheavenstay.dto.request.OwnerRequestDto;
import com.springboot.cozyheavenstay.dto.response.*;
import com.springboot.cozyheavenstay.enums.BookingStatus;
import com.springboot.cozyheavenstay.enums.HotelStatus;
import com.springboot.cozyheavenstay.enums.PaymentStatus;
import com.springboot.cozyheavenstay.enums.Role;
import com.springboot.cozyheavenstay.exception.InvalidCallException;
import com.springboot.cozyheavenstay.exception.OwnerShipException;
import com.springboot.cozyheavenstay.exception.ResourceNotFoundException;
import com.springboot.cozyheavenstay.mapper.BookingMapper;
import com.springboot.cozyheavenstay.mapper.OwnerMapper;
import com.springboot.cozyheavenstay.model.*;
import com.springboot.cozyheavenstay.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OwnerService {

    private final OwnerRepository ownerRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final HotelRepository hotelRepository;
    private final BookingRepository bookingRepository;
    private final BookingRoomsRepository bookingRoomsRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public void addOwner(OwnerRequestDto ownerRequestDto) {
        log.info("Registering new hotel owner. Username: {}", ownerRequestDto.username());

        Owner owner = OwnerMapper.convertDtoToEntity(ownerRequestDto);

        //create user object
        User user = new User();
        user.setUsername(ownerRequestDto.username());
        user.setPassword(passwordEncoder.encode(ownerRequestDto.password()));
        user.setRole(Role.OWNER);
        user = userRepository.save(user);

        owner.setUser(user);
        ownerRepository.save(owner);
        log.info("Hotel owner registered successfully. Username: {}",
                ownerRequestDto.username());
    }

    @Transactional
    public void updateHotelStatus(Long hotelId, HotelStatus hotelStatus, String ownerUsername) {
        // Find Hotel or else throw exception
        Hotels hotel = hotelRepository.findById(hotelId).orElseThrow(()->
                new ResourceNotFoundException("Hotel not found"));

        // check ownership
        if(!hotel.getOwner().getUser().getUsername().equals(ownerUsername)){
            throw new OwnerShipException("Owner does not match");
        }

        // validate status
        if(hotelStatus==null){
            throw new InvalidCallException("Hotel status is required");
        }

        //update operational status
        hotel.setHotelStatus(hotelStatus);

        //save hotel
        hotelRepository.save(hotel);
        log.info("Hotel status updated successfully. Hotel ID: {}", hotelId);
    }


    public OwnerBookingFilterDto getHotelBookings(
            Long hotelId,
            String ownerUsername,
            Integer page,
            Integer size) {

        // Validate page and size
        if (page < 0 || size < 1 || size > 100)
            throw new InvalidCallException(
                    "Page must be 0 or above, size must be between 1 and 100"
            );

        // Find hotel
        Hotels hotel = hotelRepository.findById(hotelId).orElseThrow(
                () -> new ResourceNotFoundException("Hotel not found")
        );

        // Check ownership
        if (!hotel.getOwner().getUser().getUsername().equals(ownerUsername))
            throw new OwnerShipException(
                    "You can view bookings from your own hotels only"
            );

        // Get bookings with pagination
        Pageable pageable = PageRequest.of(page, size);

        Page<Bookings> bookingPage =
                bookingRepository.getBookingsByHotelId(hotelId, pageable);

        List<OwnerBookingResponseDto> response = new ArrayList<>();

        for (Bookings booking : bookingPage.getContent()) {

            List<BookingRooms> bookingRooms =
                    bookingRoomsRepository.getRoomsByBookingId(booking.getId());

            List<BookingRoomDetailsDto> rooms = bookingRooms.stream()
                    .map(BookingMapper::convertBookingRoomToDto)
                    .toList();

            Payments payment = paymentRepository
                    .getPaymentsByBookingId(booking.getId())
                    .orElse(null);

            // Map booking to response
            response.add(
                    BookingMapper.convertEntityToOwnerDto(
                            booking, rooms, payment
                    )
            );
        }

        // Create pagination information
        PaginationDto paginationInfo = new PaginationDto(
                bookingPage.getTotalPages(),
                bookingPage.getTotalElements(),
                bookingPage.getNumberOfElements()
        );

        return new OwnerBookingFilterDto(response, paginationInfo);
    }

    public List<OwnerBookingResponseDto> getRefundRequestedBookings(String ownerUsername) {

        List<Bookings> bookings = bookingRepository.getRefundRequestedBookings(
                BookingStatus.CANCELLED,
                PaymentStatus.PAID,
                ownerUsername
        );

        List<OwnerBookingResponseDto> response = new ArrayList<>();

        for (Bookings booking : bookings) {

            List<BookingRooms> bookingRooms =
                    bookingRoomsRepository.getRoomsByBookingId(booking.getId());

            List<BookingRoomDetailsDto> rooms = bookingRooms.stream()
                    .map(BookingMapper::convertBookingRoomToDto)
                    .toList();

            Payments payment = paymentRepository
                    .getPaymentsByBookingId(booking.getId())
                    .orElse(null);

            response.add(
                    BookingMapper.convertEntityToOwnerDto(booking, rooms, payment)
            );
        }

        return response;
    }


}
