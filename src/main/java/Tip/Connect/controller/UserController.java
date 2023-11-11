package Tip.Connect.controller;

import Tip.Connect.constant.ErrorMessages;
import Tip.Connect.model.AppUser;
import Tip.Connect.model.Record;
import Tip.Connect.model.reponse.ErrorReponse;
import Tip.Connect.model.reponse.HttpReponse;
import Tip.Connect.model.reponse.SearchResponse;
import Tip.Connect.model.reponse.TinyUser;
import Tip.Connect.model.request.LoginRequest;
import Tip.Connect.model.request.UpdateRequest;
import Tip.Connect.service.AppUserService;
import Tip.Connect.service.JwtService;
import Tip.Connect.utility.DataRetrieveUtil;
import Tip.Connect.validator.EmailValidator;
import Tip.Connect.validator.PhoneValidator;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;

@RestController
@RequestMapping(path = "api/user")
@RequiredArgsConstructor
public class UserController {

    private final AppUserService appUserService;

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
        System.out.println(request);
        System.out.println(updateRequest);
        return ResponseEntity.ok(appUserService.updateUserInfo(request,updateRequest));
    }

    @PostMapping("/updateAvatar")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
        System.out.println("call update avatar");
        String fileName = file.getOriginalFilename();
        System.out.println(file);
        return ResponseEntity.ok("File uploaded successfully: " + fileName);
    }

    @GetMapping(value = "/getListFriend")
    public ResponseEntity<StreamingResponseBody> getListFriend(HttpServletRequest request){
        String userId = appUserService.getUserIdByHttpRequest(request);
        if(userId==null){
            return ResponseEntity.ok(null);
        }
        return ResponseEntity.ok(appUserService.getListFriend(userId));
    }


    @GetMapping(value = "/search/{query}&{offset}&{limit}")
    public ResponseEntity<HttpReponse> search(HttpServletRequest request,@PathVariable("query") String query, @PathVariable("offset") String offset, @PathVariable("limit") String limit) {
        System.out.println("Some one call search API");
        String userID = appUserService.getUserIdByHttpRequest(request);
        if(userID==null){
            return ResponseEntity.ok(new ErrorReponse.builder().code(ErrorMessages.USERNAME_NOT_FOUND_ERROR.getCode()).errorMessage(ErrorMessages.USERNAME_NOT_FOUND_ERROR.getMessage()).build());
        }
        //check email or number return userAim
        TinyUser aimUser = appUserService.searchAimUser(query);
        //return all message match
        List<Record> messages = appUserService.searchMessages(userID,query);
        //filter with range for messages

        //return response
        return ResponseEntity.ok(new SearchResponse.builder().code(200).tinyUser(aimUser).listMessage(messages).build());
    }


}
