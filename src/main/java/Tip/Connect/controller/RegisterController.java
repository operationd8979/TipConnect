package Tip.Connect.controller;

import Tip.Connect.model.RegisterRequest;
import Tip.Connect.service.RegistrationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/v1/registration")
@AllArgsConstructor
public class RegisterController {

    private final RegistrationService registrationService;

    @PostMapping
    public String register(@RequestBody RegisterRequest request){
        return registrationService.register(request);
    }

    @GetMapping("/confirm")
    public String confirmToken(@RequestParam String token){
        return registrationService.confirmToken(token);
    }


}
