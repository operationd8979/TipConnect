package Tip.Connect.controller;

import Tip.Connect.model.AuthenticationReponse;
import Tip.Connect.model.LoginRequest;
import Tip.Connect.service.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AppUserService appUserService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationReponse> login(@RequestBody LoginRequest request){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(),request.password()));
        return ResponseEntity.ok(appUserService.login(request));
    }

    @GetMapping("refresh")
    public ResponseEntity<AuthenticationReponse> refreshToken(@NonNull @RequestParam String refreshToken){
        return ResponseEntity.ok(appUserService.refreshToken(refreshToken));
    }

}
