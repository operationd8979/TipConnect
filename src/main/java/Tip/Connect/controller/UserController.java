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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
@RequestMapping(path = "api/user")
@RequiredArgsConstructor
public class UserController {

    private final AppUserService appUserService;

    @GetMapping(value = "/getUserInfo")
    public ResponseEntity<HttpResponse> getUserInfo(HttpServletRequest request){
        String userID = appUserService.getUserIdByHttpRequest(request);
        if(userID==null){
            return ResponseEntity.ok(new ErrorReponse.builder().code(ErrorMessages.USERNAME_NOT_FOUND_ERROR.getCode()).errorMessage(ErrorMessages.USERNAME_NOT_FOUND_ERROR.getMessage()).build());
        }
        return ResponseEntity.ok(appUserService.getUserInfo(userID));
    }

    @PostMapping(value = "/updateUserInfo")
    public ResponseEntity<HttpResponse> updateUserInfo(HttpServletRequest request,@RequestBody UpdateRequest updateRequest){
        return ResponseEntity.ok(appUserService.updateUserInfo(request,updateRequest));
    }

    @PostMapping("/updateAvatar")
    public ResponseEntity<HttpResponse> updateAvatar(HttpServletRequest request, @RequestBody UpdateAvatarRequest updateAvatarRequest) throws UnsupportedEncodingException {
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
    public ResponseEntity<HttpResponse> search(HttpServletRequest request,@PathVariable("query") String query, @PathVariable("offset") String offset, @PathVariable("limit") String limit) {
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

    @GetMapping(value = "/getFriendRequests")
    public ResponseEntity<StreamingResponseBody> getFriendRequest(HttpServletRequest request){
        String userID = appUserService.getUserIdByHttpRequest(request);
        if(userID==null){
            return ResponseEntity.ok(null);
        }
        return ResponseEntity.ok(appUserService.getFriendRequests(userID));
    }

    @GetMapping(value = "/add/{friendID}")
    public ResponseEntity<HttpResponse> addFriend(HttpServletRequest request,@PathVariable("friendID") String friendID){
        String userID = appUserService.getUserIdByHttpRequest(request);
        if(userID==null){
            return ResponseEntity.ok(new ErrorReponse.builder().code(ErrorMessages.USERNAME_NOT_FOUND_ERROR.getCode()).errorMessage(ErrorMessages.USERNAME_NOT_FOUND_ERROR.getMessage()).build());
        }
        return ResponseEntity.ok(appUserService.addFriend(userID,friendID));
    }

    @GetMapping(value = "/cancel/{friendID}")
    public ResponseEntity<HttpResponse> cancelFriendRequest(HttpServletRequest request,@PathVariable("friendID") String friendID){
        String userID = appUserService.getUserIdByHttpRequest(request);
        if(userID==null){
            return ResponseEntity.ok(new ErrorReponse.builder().code(ErrorMessages.USERNAME_NOT_FOUND_ERROR.getCode()).errorMessage(ErrorMessages.USERNAME_NOT_FOUND_ERROR.getMessage()).build());
        }
        return ResponseEntity.ok(appUserService.cancelFriendRequest(userID,friendID));
    }

    @GetMapping(value = "/acceptFriendRequest/{requestID}")
    public ResponseEntity<HttpResponse> acceptFriendRequest(HttpServletRequest request,@PathVariable("requestID") String requestID){
        String userID = appUserService.getUserIdByHttpRequest(request);
        if(userID==null){
            return ResponseEntity.ok(null);
        }
        return ResponseEntity.ok(appUserService.acceptFriendRequest(userID,requestID));
    }

    @GetMapping(value = "/denyFriendRequest/{requestID}")
    public ResponseEntity<HttpResponse> denyFriendRequest(HttpServletRequest request,@PathVariable("requestID") String requestID){
        String userID = appUserService.getUserIdByHttpRequest(request);
        if(userID==null){
            return ResponseEntity.ok(null);
        }
        return ResponseEntity.ok(appUserService.denyFriendRequest(userID,requestID));
    }

    @GetMapping(value = "/getMessages/{friendID}&{offset}&{limit}")
    public ResponseEntity<StreamingResponseBody> getMessages(HttpServletRequest request,@PathVariable("friendID") String friendID,@PathVariable("offset") String offset, @PathVariable("limit") String limit){
        System.out.println("[GET MESSAGE] offset: "+offset+" limit: "+limit);
        String userID = appUserService.getUserIdByHttpRequest(request);
        if(userID==null){
            return ResponseEntity.ok(null);
        }
        return ResponseEntity.ok(appUserService.getMessages(userID,friendID,offset,Integer.parseInt(limit)));
    }

    @GetMapping(value = "/getGifItems")
    public ResponseEntity<StreamingResponseBody> getGifItem(HttpServletRequest request){
        return ResponseEntity.ok(appUserService.getGifItem());
    }


}
