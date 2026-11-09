package com.springboot.cozyheavenstay.service;

import com.springboot.cozyheavenstay.dto.request.AdminRequestDto;
import com.springboot.cozyheavenstay.dto.response.*;
import com.springboot.cozyheavenstay.enums.BookingStatus;
import com.springboot.cozyheavenstay.enums.HotelStatus;
import com.springboot.cozyheavenstay.enums.Role;
import com.springboot.cozyheavenstay.enums.VerificationStatus;
import com.springboot.cozyheavenstay.exception.InvalidCallException;
import com.springboot.cozyheavenstay.exception.ResourceNotFoundException;
import com.springboot.cozyheavenstay.mapper.AdminMapper;
import com.springboot.cozyheavenstay.mapper.BookingMapper;
import com.springboot.cozyheavenstay.mapper.HotelMapper;
import com.springboot.cozyheavenstay.model.*;
import com.springboot.cozyheavenstay.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final HotelRepository hotelRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final BookingRoomsRepository bookingRoomsRepository;

    @Transactional
    public void addAdmin(AdminRequestDto adminRequestDto) {

        Admin admin = AdminMapper.convertDtoToEntity(adminRequestDto);

        //create user object
        User user = new User();
        user.setUsername(adminRequestDto.username());
        user.setPassword(passwordEncoder.encode(adminRequestDto.password()));
        user.setRole(Role.ADMIN);
        user = userRepository.save(user);

        admin.setUser(user);

        adminRepository.save(admin);
    }

    public List<AdminHotelResponseDto> getAllPendingHotels() {
        List<Hotels> pendingHotels = hotelRepository.getAllPendingHotels(VerificationStatus.PENDING);
        return pendingHotels.stream().map(HotelMapper::convertEntityToAdminDto).toList();
    }

    public List<AdminHotelResponseDto> getAllHotels() {

        List<Hotels> hotels = hotelRepository.findAll();

        return hotels.stream()
                .map(HotelMapper::convertEntityToAdminDto)
                .toList();
    }

    public void updateHotelVerificationStatus(Long hotelId, VerificationStatus verificationStatus) {
        Hotels hotel = hotelRepository.findById(hotelId).orElseThrow(()->
                new ResourceNotFoundException("Hotel not found"));

        //allow approval or reject only
        if(verificationStatus==null||verificationStatus==VerificationStatus.PENDING){
            throw new InvalidCallException("Select approve or reject");
        }

        //update hotel
        hotel.setVerificationStatus(verificationStatus);

        //save
        hotelRepository.save(hotel);
    }

    public List<AdminBookingResponseDto> getAllBookings(BookingStatus bookingStatus,Long hotelId, Integer page, Integer size){
        //validate pagination
        if(page<0||size<1||size>100)
            throw new InvalidCallException("Page must ne at least 0, size must be between 1 and 100");

        //validate optional hotel filter
        if(hotelId!=null){
            if(hotelId<1){
                throw new InvalidCallException("Hotel ID must be positive");
            }
            if(!hotelRepository.existsById(hotelId))
                throw new ResourceNotFoundException("Hotel not found");
        }

        //get filtered bookings
        Pageable pageable= PageRequest.of(page,size);
        List<Bookings> bookings=bookingRepository.dynamicFilterBookings(bookingStatus,hotelId,pageable);

        List<AdminBookingResponseDto> response= new ArrayList<>();

        for(Bookings booking:bookings){
            List<BookingRooms> bookingRooms= bookingRoomsRepository.getRoomsByBookingId(booking.getId());

            if(bookingRooms.isEmpty())
                throw new InvalidCallException("No rooms found for this booking");

            //all rooms in one booking belongs to the same hotel
            Hotels hotels=bookingRooms.get(0).getRoom().getHotel();

            List<BookingRoomDetailsDto> rooms = bookingRooms.stream().map(BookingMapper::convertBookingRoomToDto).toList();

            Payments payments=paymentRepository.getPaymentsByBookingId(booking.getId()).orElse(null);

            //map to admin response
            response.add(BookingMapper.convertEntityToAdminDto(booking,hotels,rooms,payments));
        }
        return response;
    }

    public List<AdminUserResponseDto> getAllUsers(Role role,Integer page,Integer size){
        if(page<0||size<1||size>100)
            throw new InvalidCallException("Page must ne at least 0, size must be between 1 and 100");

        Pageable pageable=PageRequest.of(page,size);
        return userRepository.getUserForAdmin(List.of(Role.CUSTOMER,Role.OWNER),role,pageable);

    }

    public AdminUserResponseDto updateUserStatus(Long userId,boolean active){
        //find user
        User user= userRepository.findById(userId).orElseThrow(()->
                new ResourceNotFoundException("User not found"));

        //update account status
        user.setActive(active);
        userRepository.save(user);

        return AdminMapper.convertUserToDto(user);
    }

    public AdminDashboardSummaryDto getDashboardSummary() {

        Long totalHotels = hotelRepository.count();

        Long pendingHotels = hotelRepository.countByVerificationStatus(
                VerificationStatus.PENDING
        );

        Long approvedHotels = hotelRepository.countByVerificationStatus(
                VerificationStatus.APPROVED
        );

        Long rejectedHotels = hotelRepository.countByVerificationStatus(
                VerificationStatus.REJECTED
        );

        Long activeHotels = hotelRepository.countByHotelStatus(
                HotelStatus.ACTIVE
        );

        Long inactiveHotels = hotelRepository.countByHotelStatus(
                HotelStatus.INACTIVE
        );

        Long totalBookings = bookingRepository.count();

        Long totalUsers = userRepository.count();

        Long totalCustomers = userRepository.countByRole(Role.CUSTOMER);

        Long totalOwners = userRepository.countByRole(Role.OWNER);

        return new AdminDashboardSummaryDto(
                totalHotels,
                pendingHotels,
                approvedHotels,
                rejectedHotels,
                activeHotels,
                inactiveHotels,
                totalBookings,
                totalUsers,
                totalCustomers,
                totalOwners
        );
    }

    public void updateHotelStatus(Long hotelId, HotelStatus hotelStatus) {

        // find hotel
        Hotels hotel = hotelRepository.findById(hotelId).orElseThrow(() ->
                new ResourceNotFoundException("Hotel not found"));

        // validate status
        if (hotelStatus == null) {
            throw new InvalidCallException("Select hotel status");
        }

        // update hotel status
        hotel.setHotelStatus(hotelStatus);

        // save
        hotelRepository.save(hotel);
    }
}
