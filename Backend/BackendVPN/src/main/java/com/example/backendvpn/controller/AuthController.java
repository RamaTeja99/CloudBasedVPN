package com.example.backendvpn.controller;

import com.example.backendvpn.model.User;
import com.example.backendvpn.service.JwtService;
import com.example.backendvpn.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        Authentication authentication = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, password)
        );

        User user = (User) authentication.getPrincipal();

        if (userService.isPasswordExpired(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Password expired. Please change your password.");
        }

        String jwt = jwtService.generateToken(user);
        return ResponseEntity.ok(jwt);
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password) {
        User user = userService.registerUser(username, email, password);
        return ResponseEntity.ok(user);
    }
}