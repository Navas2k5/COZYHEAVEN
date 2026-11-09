package com.springboot.cozyheavenstay.mapper;

import com.springboot.cozyheavenstay.dto.request.RoomRequestDto;
import com.springboot.cozyheavenstay.dto.response.RoomResponseDto;
import com.springboot.cozyheavenstay.model.Rooms;
import org.springframework.stereotype.Component;

@Component
public class RoomMapper {

    public static Rooms convertDtoToEntity(RoomRequestDto dto){
        Rooms room = new Rooms();
        room.setRoomNumber(dto.roomNumber().trim());
        room.setRoomSize(dto.roomSize());
        room.setMaxOccupancy(dto.maxOccupancy());
        room.setBaseFare(dto.baseFare());
        room.setBedType(dto.bedType());
        room.setAc(dto.ac());
        return room;
    }

    public static RoomResponseDto convertEntityToDto(Rooms room){
        return new RoomResponseDto(room.getId(),
                room.getRoomNumber(),
                room.getRoomSize(),
                room.getBedType(),
                room.getMaxOccupancy(),
                room.getBaseFare(),
                room.getAc(),
                room.getRoomStatus()
        );
    }

    public static void updateEntityFromDto(RoomRequestDto dto, Rooms room){
        room.setRoomNumber(dto.roomNumber().trim());
        room.setRoomSize(dto.roomSize());
        room.setBedType(dto.bedType());
        room.setMaxOccupancy(dto.maxOccupancy());
        room.setBaseFare(dto.baseFare());
        room.setAc(dto.ac());
    }
}
