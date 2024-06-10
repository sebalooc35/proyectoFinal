package org.backendada.proyectofinal.auth.controller;

import lombok.RequiredArgsConstructor;
import org.backendada.proyectofinal.auth.data.AuthenticateRequest;
import org.backendada.proyectofinal.auth.data.AuthenticationResponse;
import org.backendada.proyectofinal.auth.data.RegisterRequest;
import org.backendada.proyectofinal.auth.service.AuthenticationService;
import org.backendada.proyectofinal.auth.service.SecurityContextService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final SecurityContextService securityContextService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request){
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticateRequest request){
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
}
