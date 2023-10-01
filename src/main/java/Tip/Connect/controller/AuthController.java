package Tip.Connect.controller;

import Tip.Connect.constant.ErrorMessages;
import Tip.Connect.model.AuthenticationReponse;
import Tip.Connect.model.LoginRequest;
import Tip.Connect.service.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AppUserService appUserService;
    private final AuthenticationManager authenticationManager;


    @PostMapping("/login")
    public ResponseEntity<AuthenticationReponse> login(@RequestBody LoginRequest request){
        int code = 200;
        String errorMessage = "";
        System.out.println("Some one log:" + request.email() + " " + request.password());
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(),request.password()));
        }catch (UsernameNotFoundException ex){
            code = 403;
            errorMessage = ErrorMessages.USERNAME_NOT_FOUND_ERROR_MESSAGE;
            System.out.println("1");
        }catch (AuthenticationException ex){
            code = 403;
            errorMessage = ErrorMessages.ILLEGAL_PASSWORD_MESSAGE;
            System.out.println("2");
        }catch (Exception ex){
            code = 403;
            errorMessage = ex.getMessage();
            System.out.println("3");
        }
        if(code != 200)
            return  ResponseEntity.ok(AuthenticationReponse.builder().code(code).errorMessage(errorMessage).build());
        return ResponseEntity.ok(appUserService.login(request));
    }

    @GetMapping("refresh")
    public ResponseEntity<AuthenticationReponse> refreshToken(@NonNull @RequestParam String refreshToken){
        return ResponseEntity.ok(appUserService.refreshToken(refreshToken));
    }

}
