package Tip.Connect.controller;

import Tip.Connect.constant.ErrorMessages;
import Tip.Connect.model.Chat.Record;
import Tip.Connect.model.reponse.*;
import Tip.Connect.model.request.UpdateAvatarRequest;
import Tip.Connect.model.request.UpdateRequest;
import Tip.Connect.service.AppUserService;
import Tip.Connect.service.FireBaseService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
@RequestMapping(path = "api/user")
@RequiredArgsConstructor
public class UserController {

    private final AppUserService appUserService;
    private final FireBaseService fireBaseService;

    @GetMapping(value = "/getUserInfo")
    public ResponseEntity<HttpReponse> getUserInfo(HttpServletRequest request){
        String userID = appUserService.getUserIdByHttpRequest(request);
        if(userID==null){
            return ResponseEntity.ok(new ErrorReponse.builder().code(ErrorMessages.USERNAME_NOT_FOUND_ERROR.getCode()).errorMessage(ErrorMessages.USERNAME_NOT_FOUND_ERROR.getMessage()).build());
        }
        return ResponseEntity.ok(appUserService.getUserInfo(userID));
    }

    @PostMapping(value = "/updateUserInfo")
    public ResponseEntity<HttpReponse> updateUserInfo(HttpServletRequest request,@RequestBody UpdateRequest updateRequest){
        return ResponseEntity.ok(appUserService.updateUserInfo(request,updateRequest));
    }

    @PostMapping("/updateAvatar")
    public ResponseEntity<HttpReponse> updateAvatar(HttpServletRequest request, @RequestBody UpdateAvatarRequest updateAvatarRequest) throws UnsupportedEncodingException {
        return ResponseEntity.ok(appUserService.updateAvatar(request,updateAvatarRequest.urlAvatar()));
    }

    @GetMapping(value = "/getListFriend")
    public ResponseEntity<StreamingResponseBody> getListFriend(HttpServletRequest request){
        String userID = appUserService.getUserIdByHttpRequest(request);
        if(userID==null){
            return ResponseEntity.ok(null);
        }
        return ResponseEntity.ok(appUserService.getListFriend(userID));
    }


    @GetMapping(value = "/search/{query}&{offset}&{limit}")
    public ResponseEntity<HttpReponse> search(HttpServletRequest request,@PathVariable("query") String query, @PathVariable("offset") String offset, @PathVariable("limit") String limit) {
        String userID = appUserService.getUserIdByHttpRequest(request);
        if(userID==null){
            return ResponseEntity.ok(new ErrorReponse.builder().code(ErrorMessages.USERNAME_NOT_FOUND_ERROR.getCode()).errorMessage(ErrorMessages.USERNAME_NOT_FOUND_ERROR.getMessage()).build());
        }
        //check email or number return userAim
        TinyUser aimUser = appUserService.searchAimUser(userID,query);
        //return all message match
        List<Record> messages = appUserService.searchMessages(userID,query);
        //filter with range for messages

        //return response
        return ResponseEntity.ok(new SearchResponse.builder().code(200).tinyUser(aimUser).listMessage(messages).build());
    }

    @GetMapping(value = "/add/{friendID}")
    public ResponseEntity<HttpReponse> addFriend(HttpServletRequest request,@PathVariable("friendID") String friendID){
        String userID = appUserService.getUserIdByHttpRequest(request);
        if(userID==null){
            return ResponseEntity.ok(new ErrorReponse.builder().code(ErrorMessages.USERNAME_NOT_FOUND_ERROR.getCode()).errorMessage(ErrorMessages.USERNAME_NOT_FOUND_ERROR.getMessage()).build());
        }
        appUserService.addFriend(userID,friendID);
        return ResponseEntity.ok(new MessageResponse(200,"OK"));
    }

    @GetMapping(value = "/getFriendRequests")
    public ResponseEntity<StreamingResponseBody> getFriendRequest(HttpServletRequest request){
        String userID = appUserService.getUserIdByHttpRequest(request);
        if(userID==null){
            return ResponseEntity.ok(null);
        }
        return ResponseEntity.ok(appUserService.getFriendRequests(userID));
    }


}
