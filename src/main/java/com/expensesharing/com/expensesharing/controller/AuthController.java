package com.expensesharing.com.expensesharing.controller;

import com.expensesharing.com.expensesharing.dto.AuthResponse;
import com.expensesharing.com.expensesharing.dto.LoginRequest;
import com.expensesharing.com.expensesharing.dto.RegisterRequest;
import com.expensesharing.com.expensesharing.entity.AuthAccount;
import com.expensesharing.com.expensesharing.entity.Role;
import com.expensesharing.com.expensesharing.repositories.AuthAccountRepository;
import com.expensesharing.com.expensesharing.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Register and log in to obtain a JWT")
public class AuthController {

    private final AuthAccountRepository authAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    public AuthController(AuthAccountRepository authAccountRepository,
                          PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager,
                          UserDetailsService userDetailsService,
                          JwtService jwtService) {
        this.authAccountRepository = authAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new account and receive a JWT")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        if (authAccountRepository.existsByUsername(request.getUsername())) {
            throw new ValidationException("Username already exists");
        }

        AuthAccount account = new AuthAccount(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                Role.USER);
        authAccountRepository.save(account);

        UserDetails userDetails = userDetailsService.loadUserByUsername(account.getUsername());
        String token = jwtService.generateToken(userDetails);
        return new ResponseEntity<>(new AuthResponse(token, account.getUsername()), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate with username/password and receive a JWT")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        } catch (BadCredentialsException ex) {
            throw new ValidationException("Invalid username or password");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtService.generateToken(userDetails);
        return new ResponseEntity<>(new AuthResponse(token, request.getUsername()), HttpStatus.OK);
    }
}
