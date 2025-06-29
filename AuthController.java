package com.airtribe.jobscheduler.controller;

import com.airtribe.jobscheduler.dto.ApiResponse;
import com.airtribe.jobscheduler.dto.AuthRequest;
import com.airtribe.jobscheduler.dto.AuthResponse;
import com.airtribe.jobscheduler.entity.User;
import com.example.jobscheduler.repository.UserRepository;
import com.airtribe.jobscheduler.security.JwtTokenProvider;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateUser(@Valid @RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody AuthRequest authRequest) {
        if (userRepository.existsByUsername(authRequest.getUsername())) {
            return new ResponseEntity<>(new ApiResponse(false, "Username is already taken!"), HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setUsername(authRequest.getUsername());
        user.setPassword(passwordEncoder.encode(authRequest.getPassword()));

        userRepository.save(user);

        return ResponseEntity.ok(new ApiResponse(true, "User registered successfully"));
    }
}