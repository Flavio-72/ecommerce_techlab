package com.techlab.ecommerce.controller;

import com.techlab.ecommerce.dto.LoginRequestDTO;
import com.techlab.ecommerce.dto.LoginResponseDTO;
import com.techlab.ecommerce.dto.RegisterRequestDTO;
import com.techlab.ecommerce.dto.RegisterResponseDTO;
import com.techlab.ecommerce.model.entity.AppUser;
import com.techlab.ecommerce.service.AppUserService;
import com.techlab.ecommerce.service.AppUserDetailsService;
import com.techlab.ecommerce.util.JwtUtil;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v0.1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final AppUserDetailsService userDetailsService;
    private final AppUserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager,
            JwtUtil jwtUtil,
            AppUserDetailsService userDetailsService,
            AppUserService userService,
            PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@RequestBody RegisterRequestDTO request) {
        AppUser newUser = new AppUser();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(request.getPassword());
        newUser.setRoles(request.getRoles());

        AppUser savedUser = userService.registerUser(newUser);

        RegisterResponseDTO response = new RegisterResponseDTO(
                savedUser.getId(), savedUser.getUsername(), savedUser.getEmail());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        try {
            userService.verifyPassword(loginRequest.getUsername(), loginRequest.getPassword());

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(), loginRequest.getPassword());

            Authentication authentication = authenticationManager.authenticate(authToken);

            var userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
            String token = jwtUtil.generateToken(userDetails.getUsername());

            return ResponseEntity.ok(new LoginResponseDTO(token, userDetails.getUsername()));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(401).body("Credenciales inválidas: " + ex.getMessage());
        }
    }
}
