package Tip.Connect.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/home")
public class HomeController {

    @GetMapping
    public ResponseEntity<String> home(){
        System.out.println("Someone home!");
        return ResponseEntity.ok("OK");
    }

}
