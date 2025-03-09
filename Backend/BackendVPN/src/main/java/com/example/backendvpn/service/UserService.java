package com.example.backendvpn.service;

import com.example.backendvpn.model.User;
import com.example.backendvpn.model.UserCredentials;
import com.example.backendvpn.repository.UserRepository;


import com.example.backendvpn.repository.UserCredentialsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserCredentialsRepository userCredentialsRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(String username, String email, String password) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setRole(User.Role.USER); // Set the role using the enum

        UserCredentials credentials = new UserCredentials();
        credentials.setPassword(passwordEncoder.encode(password));
        credentials.setUser(user);
        user.setCredentials(credentials);

        return userRepository.save(user);
    }

    public boolean isPasswordExpired(User user) {
        return user.getCredentials().isPasswordExpired();
    }
}