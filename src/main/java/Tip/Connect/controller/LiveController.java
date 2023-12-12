package Tip.Connect.controller;

import Tip.Connect.service.AppUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping(path = "api/user/live")
@RequiredArgsConstructor
public class LiveController {

    public static List<String> liveList = new ArrayList<>();
    public static List<String> watchList = new ArrayList<>();
    private final AppUserService appUserService;

    @GetMapping(value = "/getListLive")
    public ResponseEntity<StreamingResponseBody> getListLive(HttpServletRequest request){
        String userID = appUserService.getUserIdByHttpRequest(request);
        if(userID==null){
            return ResponseEntity.ok(null);
        }
        return ResponseEntity.ok(appUserService.getListLive(userID));
    }


}
