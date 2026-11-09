package com.springboot.cozyheavenstay.controller;

import com.springboot.cozyheavenstay.dto.response.PaymentResponseDto;
import com.springboot.cozyheavenstay.dto.response.RefundResponseDto;
import com.springboot.cozyheavenstay.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173/")
public class PaymentController{

    private final PaymentService paymentService;

    @PostMapping("/api/payment/booking/{bookingId}/pay")
    public PaymentResponseDto pay(@PathVariable Long bookingId, Principal principal){
        return paymentService.pay(bookingId, principal.getName());
    }

    @PutMapping("/api/payment/booking/{bookingId}/refund")
    public RefundResponseDto refundBooking(@PathVariable Long bookingId,
                                            Principal principal){
        return paymentService.refundBooking(bookingId, principal.getName());
    }

}
