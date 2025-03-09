package com.example.backendvpn.controller;

import com.example.backendvpn.model.User;
import com.example.backendvpn.service.JwtService;
import com.example.backendvpn.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password, HttpServletResponse response) {
        try {
            Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );

            User user = (User) authentication.getPrincipal();

            if (userService.isPasswordExpired(user)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("❌ Password expired. Please change your password.");
            }

            String jwt = jwtService.generateToken(user);

            
            Cookie cookie = new Cookie("jwt", jwt);
            cookie.setHttpOnly(true);
            cookie.setSecure(true); 
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 24); 
            response.addCookie(cookie);
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("token", jwt);
            responseBody.put("role", user.getRole().name());
            responseBody.put("username", user.getUsername());

            return ResponseEntity.ok(responseBody);
        } catch (Exception e) {
            System.out.println("❌ Authentication failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password.");
        }
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password) {

        if (userService.userExists(username)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("❌ Username already taken.");
        }

        User user = userService.registerUser(username, email, password);
        return ResponseEntity.ok("✅ User registered successfully.");
    }
}
