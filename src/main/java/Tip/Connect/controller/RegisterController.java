package Tip.Connect.controller;

import Tip.Connect.model.ErrorReponse;
import Tip.Connect.model.HttpReponse;
import Tip.Connect.model.RegisterRequest;
import Tip.Connect.service.RegistrationService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/v1/registration")
@RequiredArgsConstructor
public class RegisterController {

    private final RegistrationService registrationService;

    @PostMapping
    public ResponseEntity<HttpReponse> register(HttpServletResponse httpServletResponse, @RequestBody RegisterRequest request){
        System.out.println("someone register: "+ request.getEmail() + " " +request.getPassword()+ " " + request.getFirstName() + " " + request.getLastName());
        return registrationService.register(httpServletResponse,request);
    }

    @GetMapping("/confirm")
    public ResponseEntity<String> confirmToken(@RequestParam String token){
        return registrationService.confirmToken(token);
    }

    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }

}