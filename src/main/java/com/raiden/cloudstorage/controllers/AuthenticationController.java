package com.raiden.cloudstorage.controllers;

import com.raiden.cloudstorage.dto.AuthenticationRequest;
import com.raiden.cloudstorage.dto.AuthenticationResponse;
import com.raiden.cloudstorage.services.AuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/authenticate")
@AllArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authService;

    @PostMapping
    public AuthenticationResponse createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest){
        return authService.authenticateUser(authenticationRequest);
    }
}
