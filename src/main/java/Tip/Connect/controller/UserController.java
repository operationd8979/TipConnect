package Tip.Connect.controller;

import Tip.Connect.service.AppUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequestMapping(path = "api/user")
@RequiredArgsConstructor
public class UserController {

    private final AppUserService appUserService;

    @GetMapping(value = "/getListFriend/{id}")
    public ResponseEntity<StreamingResponseBody> getListFriend(@PathVariable("id") String userId){
        System.out.println("Someone get list friend "+ userId);
        return ResponseEntity.ok(appUserService.getListFriend(userId));
    }


}
