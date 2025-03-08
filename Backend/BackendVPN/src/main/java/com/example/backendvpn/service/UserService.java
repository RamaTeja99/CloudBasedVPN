package com.example.backendvpn.service;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.backendvpn.dto.UserRegistrationDto;
import com.example.backendvpn.model.*;
import com.example.backendvpn.repository.*;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordHistoryRepository passwordHistoryRepo;
    private final PasswordEncoder passwordEncoder;
    public UserService(UserRepository userRepository, PasswordHistoryRepository passwordHistoryRepo, PasswordEncoder passwordEncoder) {
    	this.passwordEncoder=passwordEncoder;
    	this.userRepository=userRepository;
    	this.passwordHistoryRepo=passwordHistoryRepo;
    }
    
    public User registerUser(UserRegistrationDto dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setRole(Role.USER);
        
        UserCredentials credentials = new UserCredentials();
        credentials.setPassword(passwordEncoder.encode(dto.getPassword()));
        credentials.setUser(user);
        user.setCredentials(credentials);
        
        return userRepository.save(user);
    }
    
    public void changePassword(User user, String newPassword) throws Exception {
        java.util.List<PasswordHistory> oldPasswords = passwordHistoryRepo
            .findTop5ByUserOrderByChangedAtDesc(user);
        
        for (PasswordHistory ph : oldPasswords) {
            if (passwordEncoder.matches(newPassword, ph.getPassword())) {
                throw new Exception("Cannot reuse previous passwords");
            }
        }
        
        PasswordHistory history = new PasswordHistory();
        history.setUser(user);
        history.setPassword(user.getCredentials().getPassword());
        passwordHistoryRepo.save(history);
        
        user.getCredentials().setPassword(passwordEncoder.encode(newPassword));
        user.getCredentials().setLastPasswordChange(LocalDateTime.now());
        userRepository.save(user);
    }

	public boolean isPasswordExpired(User user) {
		// TODO Auto-generated method stub
		return false;
	}
}