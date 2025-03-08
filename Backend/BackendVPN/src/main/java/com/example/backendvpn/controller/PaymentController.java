package com.example.backendvpn.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.backendvpn.repository.*;
import com.example.backendvpn.service.*;
import com.razorpay.RazorpayException;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final RazorpayService razorpayService;
    private final PaymentRepository paymentRepository;
    public PaymentController(RazorpayService razorpayService,PaymentRepository paymentRepository) {
    	this.razorpayService=razorpayService;
    	this.paymentRepository=paymentRepository;
    }
    
    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody PaymentRequest request) {
        try {
            String orderId = razorpayService.createOrder(request.getAmount());
            return ResponseEntity.ok(new OrderResponse(orderId));
        } catch (RazorpayException e) {
            return ResponseEntity.internalServerError().body("Payment gateway error");
        }
    }
    
    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody PaymentVerificationRequest request) {
        boolean isValid = razorpayService.verifyPayment(
            request.getOrderId(),
            request.getPaymentId(),
            request.getSignature()
        );
        
        if (isValid) {
            // Save payment and activate subscription
            return ResponseEntity.ok("Payment verified successfully");
        }
        
        return ResponseEntity.badRequest().body("Invalid payment signature");
    }
}
