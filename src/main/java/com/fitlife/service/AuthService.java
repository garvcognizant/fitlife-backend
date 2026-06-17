package com.fitlife.service;

import com.fitlife.dto.*;
import com.fitlife.exception.BadRequestException;
import com.fitlife.exception.ConflictException;
import com.fitlife.exception.NotFoundException;
import com.fitlife.model.User;
import com.fitlife.repository.UserRepository;
import com.fitlife.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already registered");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .securityQuestion(request.getSecurityQuestion())
                .securityAnswer(passwordEncoder.encode(request.getSecurityAnswer().toLowerCase().trim()))
                .build();

        user = userRepository.save(user);
        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail());
        return new AuthResponse(token, UserDto.from(user));
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid email or password");
        }

        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail());
        return new AuthResponse(token, UserDto.from(user));
    }

    public String getSecurityQuestion(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("No account found with this email"));
        return user.getSecurityQuestion();
    }

    @Transactional
    public void verifySecurityAnswerAndResetPassword(String email, String securityAnswer, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("No account found with this email"));

        if (user.getSecurityAnswer() == null ||
            !passwordEncoder.matches(securityAnswer.toLowerCase().trim(), user.getSecurityAnswer())) {
            throw new BadRequestException("Security answer is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
