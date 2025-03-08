package com.example.backendvpn.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.backendvpn.dto.LoginRequest;
import com.example.backendvpn.dto.UserRegistrationDto;
import com.example.backendvpn.model.User;
import com.example.backendvpn.service.JwtService;
import com.example.backendvpn.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
    private  AuthenticationManager authManager;
	@Autowired
    private  JwtService jwtService;
	@Autowired
    private  UserService userService;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Authentication authentication = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );
        
        User user = (User) authentication.getPrincipal();
        
        if (userService.isPasswordExpired(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Password expired. Please change your password.");
        }
        
        String jwt = jwtService.generateToken(user);
        return ResponseEntity.ok(new JwtResponse(jwt));
    }
    
    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody UserRegistrationDto dto) {
        return ResponseEntity.ok(userService.registerUser(dto));
    }
}
