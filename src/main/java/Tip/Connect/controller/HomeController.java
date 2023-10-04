package Tip.Connect.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api")
@RequiredArgsConstructor
public class HomeController {
    @GetMapping("/hello")
    public ResponseEntity<String> hello(){
        System.out.println("someone say hello");
        return ResponseEntity.ok("OKKKK");
    }
}
