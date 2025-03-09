package com.example.backendvpn.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.backendvpn.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin
public class PaymentController {
    @Autowired
    private  PaymentService paymentService;


    @PostMapping("/create-order/{userId}")
    public ResponseEntity<?> createOrder(
            @PathVariable Long userId,
            @RequestParam double amount,
            @RequestParam(required = false) String referralCode) {
        try {
            String orderId = paymentService.createOrder(userId, amount, referralCode);
            return ResponseEntity.ok(orderId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/success")
    public ResponseEntity<?> handlePaymentSuccess(
            @RequestParam String razorpayOrderId,
            @RequestParam String razorpayPaymentId,
            @RequestParam String razorpaySignature) {
        try {
            paymentService.handlePaymentSuccess(razorpayOrderId, razorpayPaymentId, razorpaySignature);
            return ResponseEntity.ok("Payment verified and premium status updated.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}