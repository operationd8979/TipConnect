package Tip.Connect.controller;

import Tip.Connect.constant.ErrorMessages;
import Tip.Connect.model.reponse.AuthenticationReponse;
import Tip.Connect.model.reponse.ErrorReponse;
import Tip.Connect.model.reponse.HttpResponse;
import Tip.Connect.model.request.LoginRequest;
import Tip.Connect.service.AppUserService;
import Tip.Connect.service.FireBaseService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping(path = "api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AppUserService appUserService;
    private final AuthenticationManager authenticationManager;

    private final FireBaseService fireBaseService;


    @PostMapping("/login")
    public ResponseEntity<HttpResponse> login(@RequestBody LoginRequest request, HttpServletResponse reponse){
        int code = 200;
        String errorMessage = "";
        System.out.println("Some one log: " + request.email() + " " + request.password());
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(),request.password()));
        }catch (UsernameNotFoundException ex){
            code = ErrorMessages.USERNAME_NOT_FOUND_ERROR.getCode();
            errorMessage = ErrorMessages.USERNAME_NOT_FOUND_ERROR.getMessage();
        }catch (AuthenticationException ex){
            code = ErrorMessages.ILLEGAL_PASSWORD.getCode();
            errorMessage = ErrorMessages.ILLEGAL_PASSWORD.getMessage();
        }catch (Exception ex){
            code = ErrorMessages.UNKNOWN_EXCEPTION.getCode();
            errorMessage = ex.getMessage();
        }
        if(code != 200){
            return  ResponseEntity.ok(new ErrorReponse.builder().code(code).errorMessage(errorMessage).build());
        }
        return ResponseEntity.ok(appUserService.login(request,reponse));
    }

    @GetMapping("refresh")
    public ResponseEntity<AuthenticationReponse> refreshToken(@NonNull @RequestParam String refreshToken){
        return ResponseEntity.ok(appUserService.refreshToken(refreshToken));
    }

    @GetMapping("/hello")
    public String hello(){
        System.out.println("someone say hello");
        return "hello";
    }

    @GetMapping("/get")
    public String getString(@RequestParam String id) throws InterruptedException, ExecutionException {
        return fireBaseService.getUser(id);
    }

}
