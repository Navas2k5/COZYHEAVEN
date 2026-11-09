package com.springboot.cozyheavenstay.service;

import com.springboot.cozyheavenstay.dto.request.HotelRequestDto;
import com.springboot.cozyheavenstay.dto.response.CustomerHotelResponseDto;
import com.springboot.cozyheavenstay.dto.response.FilterDto;
import com.springboot.cozyheavenstay.dto.response.HotelResponseDto;
import com.springboot.cozyheavenstay.dto.response.PaginationDto;
import com.springboot.cozyheavenstay.enums.BookingStatus;
import com.springboot.cozyheavenstay.enums.HotelStatus;
import com.springboot.cozyheavenstay.enums.RoomStatus;
import com.springboot.cozyheavenstay.enums.VerificationStatus;
import com.springboot.cozyheavenstay.exception.InvalidCallException;
import com.springboot.cozyheavenstay.exception.OwnerShipException;
import com.springboot.cozyheavenstay.exception.ResourceNotFoundException;
import com.springboot.cozyheavenstay.mapper.HotelMapper;
import com.springboot.cozyheavenstay.model.Hotels;
import com.springboot.cozyheavenstay.model.Owner;
import com.springboot.cozyheavenstay.repository.HotelRepository;
import com.springboot.cozyheavenstay.repository.OwnerRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;
    private final OwnerRepository ownerRepository;

    private final static String uploadDir =
            "D:/CozyHeaven/cozyheaven_frontend/public/images/";

    @Transactional
    public Long add(String ownerUsername, HotelRequestDto hotelRequestDto){
        Owner owner= ownerRepository.getByUsername(ownerUsername);

        if(owner==null)
            throw new ResourceNotFoundException("Owner does not exist");

        Hotels hotel = HotelMapper.convertDtoToEntity(hotelRequestDto);
        hotel.setVerificationStatus(VerificationStatus.PENDING);
        hotel.setHotelStatus(HotelStatus.ACTIVE);

        hotel.setOwner(owner);

        hotelRepository.save(hotel);
        return hotel.getId();
    }

    public List<HotelResponseDto> getHotelByOwnerUsername(String ownerUsername) {
        List<Hotels> hotels = hotelRepository.getHotelByOwnerUsername(ownerUsername);

        return hotels.stream().map(HotelMapper::convertEntityToDto).toList();
    }

    public void updateHotel(Long hotelId, @Valid HotelRequestDto hotelRequestDto, String ownerUsername) {
        //get hotel
        Hotels hotel = hotelRepository.findById(hotelId).orElseThrow(()->
                new ResourceNotFoundException("Hotel Not found"));

        //check ownership
        if(!hotel.getOwner().getUser().getUsername().equals(ownerUsername))
            throw new OwnerShipException("U are not allowed to update this hotel");

        HotelMapper.updateEntityFromDto(hotelRequestDto,hotel);
        hotel.setVerificationStatus(VerificationStatus.PENDING);
        hotelRepository.save(hotel);

    }

    public FilterDto getAllHotels(String location, String hotelName, LocalDate checkIn, LocalDate checkOut, Integer page, Integer size) {

        if((checkIn==null && checkOut!=null)||(checkIn!=null && checkOut==null))
            throw new InvalidCallException("Both check-in and check-out dates are required");

        if(checkIn!=null){
            LocalDate today= LocalDate.now();
            if(checkIn.isBefore(today))
                throw new InvalidCallException("Check-In cannot be in the past");

            if(!checkOut.isAfter(checkIn))
                throw new InvalidCallException("Check-out must be after Check-in");
        }
        //validate pagination
        if(page<0||size<1||size>100)
            throw new InvalidCallException("Page must be 0 or above and size must be between 1 and 100");

        //prepare pagination
        Pageable pageable= PageRequest.of(page,size);

        //fetch approved and active hotels
        Page<CustomerHotelResponseDto> pageDto = hotelRepository.getAvailableHotels(
                VerificationStatus.APPROVED,
                HotelStatus.ACTIVE,
                location,
                hotelName,
                checkIn,
                checkOut,
                RoomStatus.ACTIVE,
                BookingStatus.PENDING,
                BookingStatus.CONFIRMED,
                pageable
        );

        return new FilterDto(
                pageDto.getContent(),
                new PaginationDto(
                        pageDto.getTotalPages(),
                        pageDto.getTotalElements(),
                        pageDto.getContent().size()
                )
        );    }

    public CustomerHotelResponseDto getHotelById(Long hotelId) {
        //get hotel
        Hotels hotel = hotelRepository.findById(hotelId).orElseThrow(()->
                new ResourceNotFoundException("Hotel Not Found"));

        //check whether it is approved and active
        if(hotel.getVerificationStatus()!=VerificationStatus.APPROVED || hotel.getHotelStatus()!=HotelStatus.ACTIVE)
            throw new ResourceNotFoundException("Hotel not found");

        //return hotels
        return HotelMapper.convertEntityToCustomerHotelResponseDto(hotel);
    }

    public HotelResponseDto getHotelByIdAndOwnerUsername(
            Long hotelId,
            String ownerUsername) {

        Hotels hotel = hotelRepository
                .getHotelByIdAndOwnerUsername(hotelId, ownerUsername);

        if (hotel == null)
            throw new ResourceNotFoundException("Hotel not found");

        return HotelMapper.convertEntityToDto(hotel);
    }

    public void upload(Long hotelId, String ownerUsername,
                       MultipartFile file) throws IOException {

        // Fetch hotel
        Hotels hotel = hotelRepository.findById(hotelId).orElseThrow(() ->
                new ResourceNotFoundException("Hotel not found"));

        // Check ownership
        if (!hotel.getOwner().getUser().getUsername().equals(ownerUsername))
            throw new OwnerShipException("You are not allowed to upload image for this hotel");

        // Validate file
        if (file == null || file.isEmpty())
            throw new NullPointerException("Please upload a valid file");

        // Generate unique filename
        String originalFileName = System.currentTimeMillis() + "_" +
                file.getOriginalFilename();

        // Copy file to images directory
        Files.copy(
                file.getInputStream(),
                Paths.get(uploadDir + originalFileName),
                StandardCopyOption.REPLACE_EXISTING
        );

        // Save filename in database
        hotel.setImageUrl(originalFileName);

        hotelRepository.save(hotel);
    }
}
