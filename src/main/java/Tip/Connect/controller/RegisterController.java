package Tip.Connect.controller;

import Tip.Connect.model.reponse.HttpReponse;
import Tip.Connect.model.request.RegisterRequest;
import Tip.Connect.service.AppUserService;
import Tip.Connect.service.RegistrationService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequestMapping(path = "api/v1/registration")
@RequiredArgsConstructor
public class RegisterController {

    private final RegistrationService registrationService;
    private final AppUserService appUserService;

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

    @GetMapping(value = "/getListFriend/{id}")
    public ResponseEntity<StreamingResponseBody> getListFriend(@PathVariable("id") String userId){
        System.out.println("Someone get list friend "+ userId);
        return ResponseEntity.ok(appUserService.getListFriend(userId));
    }

}
