package org.backendada.proyectofinal.auth.service;

import lombok.RequiredArgsConstructor;
import org.backendada.proyectofinal.auth.data.AuthenticateRequest;
import org.backendada.proyectofinal.auth.data.AuthenticationResponse;
import org.backendada.proyectofinal.auth.data.RegisterRequest;
import org.backendada.proyectofinal.config.JwtService;
import org.backendada.proyectofinal.user.entity.Role;
import org.backendada.proyectofinal.user.entity.User;
import org.backendada.proyectofinal.user.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        User user = User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        userRepository.save(user);

        String jwt = jwtService.generateToken(user);
        return new AuthenticationResponse(jwt);
    }

    public AuthenticationResponse authenticate(AuthenticateRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        return new AuthenticationResponse(
                jwtService.generateToken(
                        userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new UsernameNotFoundException("User not found"))
                )
        );
    }
}
