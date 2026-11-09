package com.springboot.cozyheavenstay.mapper;

import com.springboot.cozyheavenstay.dto.request.ReviewRequestDto;
import com.springboot.cozyheavenstay.dto.response.PublicReviewResponseDto;
import com.springboot.cozyheavenstay.dto.response.ReviewResponseDto;
import com.springboot.cozyheavenstay.model.Reviews;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {

    public static Reviews convertDtoToEntity(ReviewRequestDto dto){
        Reviews review=new Reviews();
        review.setRating(dto.rating());
        review.setComment(dto.comment());
        return review;
    }

    public static ReviewResponseDto convertEntityToDto(Reviews review){
        return new ReviewResponseDto(
                review.getId(),
                review.getBooking().getId(),
                review.getBooking().getCustomer().getName(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
        );
    }

}
