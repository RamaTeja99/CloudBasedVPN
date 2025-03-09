package com.example.backendvpn.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.apache.commons.codec.digest.HmacUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

@Service
public class PaymentService {
    @Value("${razorpay.key.id}")
    private String keyId;
    
    @Value("${razorpay.key.secret}")
    private String keySecret;

    // Method to create an order with referral code
    public String createOrder(Long userId, double amount, String referralCode) throws RazorpayException {
        RazorpayClient client = new RazorpayClient(keyId, keySecret);

        // Apply referral discount (example: 10% discount)
        BigDecimal finalAmount = applyReferralDiscount(amount, referralCode);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", finalAmount.multiply(new BigDecimal("100"))); // Convert to paise
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "order_rcpt_" + UUID.randomUUID());
        orderRequest.put("payment_capture", 1);

        // Add userId and referral code to notes (optional)
        JSONObject notes = new JSONObject();
        notes.put("userId", userId);
        notes.put("referralCode", referralCode);
        orderRequest.put("notes", notes);

        Order order = client.orders.create(orderRequest);
        return order.get("id");
    }

    // Method to apply referral discount
    private BigDecimal applyReferralDiscount(double amount, String referralCode) {
        BigDecimal originalAmount = BigDecimal.valueOf(amount);

        // Example: Apply 10% discount if referral code is valid
        if (isValidReferralCode(referralCode)) {
            BigDecimal discount = originalAmount.multiply(BigDecimal.valueOf(0.10)); // 10% discount
            return originalAmount.subtract(discount);
        }

        return originalAmount;
    }

    // Method to validate referral code (dummy implementation)
    private boolean isValidReferralCode(String referralCode) {
        // Replace with actual logic to validate referral code (e.g., check in database)
        return referralCode != null && !referralCode.isEmpty();
    }

    // Method to verify payment
    public boolean verifyPayment(String orderId, String paymentId, String signature) {
        String payload = orderId + "|" + paymentId;
        String generatedSignature = HmacUtils.hmacSha256Hex(keySecret, payload);
        return generatedSignature.equals(signature);
    }

    // Method to handle payment success
    public void handlePaymentSuccess(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {
        if (verifyPayment(razorpayOrderId, razorpayPaymentId, razorpaySignature)) {
            // Update premium status or perform other actions
            System.out.println("Payment successful for order: " + razorpayOrderId);
        } else {
            throw new RuntimeException("Invalid payment signature");
        }
    }
}