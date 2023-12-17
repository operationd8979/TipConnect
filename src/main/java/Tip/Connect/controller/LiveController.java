package Tip.Connect.controller;

import Tip.Connect.model.Live.LiveShow;
import Tip.Connect.model.request.AddGroupRequest;
import Tip.Connect.model.request.OpenLiveRequest;
import Tip.Connect.service.AppUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping(path = "api/user/live")
@RequiredArgsConstructor
public class LiveController {

    public static List<LiveShow> liveList = new ArrayList<>();
    private final AppUserService appUserService;

    @GetMapping(value = "/getListLive")
    public ResponseEntity<StreamingResponseBody> getListLive(HttpServletRequest request){
        String userID = appUserService.getUserIdByHttpRequest(request);
        if(userID==null){
            return ResponseEntity.ok(null);
        }
        return ResponseEntity.ok(appUserService.getListLive(userID));
    }

    @PostMapping(value = "/openLive")
    public ResponseEntity<String> openLive(HttpServletRequest request, @RequestBody OpenLiveRequest openLiveRequest){
        String userID = appUserService.getUserIdByHttpRequest(request);
        if(userID==null){
            return ResponseEntity.ok("ERROR");
        }
        liveList.add(new LiveShow(userID));
        return ResponseEntity.ok("OK");
    }


}
