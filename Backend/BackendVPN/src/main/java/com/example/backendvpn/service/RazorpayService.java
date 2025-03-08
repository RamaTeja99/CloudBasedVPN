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
public class RazorpayService {
    @Value("${razorpay.key.id}")
    private String keyId;
    
    @Value("${razorpay.key.secret}")
    private String keySecret;
    
    public String createOrder(BigDecimal amount) throws RazorpayException {
        RazorpayClient client = new RazorpayClient(keyId, keySecret);
        
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amount.multiply(new BigDecimal("100")));
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "order_rcpt_" + UUID.randomUUID());
        orderRequest.put("payment_capture", 1);
        Order order = client.orders.create(orderRequest);
        return order.get("id");
    }
    
    public boolean verifyPayment(String orderId, String paymentId, String signature) {
        String payload = orderId + "|" + paymentId;
        String generatedSignature = HmacUtils.hmacSha256Hex(keySecret, payload);
        return generatedSignature.equals(signature);
    }
}
