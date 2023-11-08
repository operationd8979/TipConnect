package Tip.Connect.controller;

import Tip.Connect.model.reponse.HttpReponse;
import Tip.Connect.service.AppUserService;
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


    @GetMapping(value = "/search")
    public ResponseEntity<HttpReponse> search(@PathVariable("query") String query, @PathVariable("offset") String offset, @PathVariable("limit") String limit){
        //http://localhost:8080/api/user/search?query={query}&offset={offset}&limit={limit}
        return ResponseEntity.ok(null);
    }


}
