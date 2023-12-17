package Tip.Connect.controller;

import Tip.Connect.constant.ErrorMessages;
import Tip.Connect.model.Chat.WsRecord.RawChat;
import Tip.Connect.model.Relationship.TypeRelationShip;
import Tip.Connect.model.reponse.*;
import Tip.Connect.model.request.AddGroupRequest;
import Tip.Connect.model.request.UpdateAvatarRequest;
import Tip.Connect.model.request.UpdateRequest;
import Tip.Connect.service.AppUserService;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

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
    public ResponseEntity<StreamingResponseBody> search(HttpServletRequest request,@PathVariable("query") String query, @PathVariable("offset") int offset, @PathVariable("limit") int limit) {
        String userID = appUserService.getUserIdByHttpRequest(request);
        if(userID==null){
            return ResponseEntity.ok(null);
        }
        StreamingResponseBody stream = outputStream -> {
            TinyUser aimUser = appUserService.searchAimUser(userID,query);
            List<RawChat> messages = appUserService.searchMessages(userID,query);
            int currentOffset = 0;

            if(offset==0){
                int length = messages.size();
                int end = Math.min(limit, length);
                messages = messages.subList(0, end);
                if(end==limit){
                    currentOffset = end;
                }
            }
            else{
                messages = messages.subList(offset,messages.size());
                int length = messages.size();
                int end = Math.min(limit, length);
                messages = messages.subList(0, end);
                if(end==limit){
                    currentOffset = offset+limit;
                }
            }
            ObjectMapper objectMapper = new ObjectMapper();
            JsonGenerator jsonGenerator = objectMapper.getFactory().createGenerator(outputStream);
            jsonGenerator.writeStartArray();

            jsonGenerator.writeObject(new SearchResponse.builder().code(200).tinyUser(aimUser).offset(currentOffset).build());
            Stream<RawChat> rawChatStream = messages.stream();
            if(rawChatStream!=null){
                try{
                    Iterator<RawChat> rawChatIterator = rawChatStream.iterator();
                    jsonGenerator.writeStartArray();
                    while(rawChatIterator.hasNext()) {
                        RawChat rawChat = rawChatIterator.next();
                        jsonGenerator.writeObject(rawChat);
                    }
                    jsonGenerator.writeEndArray();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
            jsonGenerator.writeEndArray();
            if(rawChatStream != null) {
                rawChatStream.close();
            }
            if(jsonGenerator != null)  {
                jsonGenerator.close();
            }
        };
        return ResponseEntity.ok(stream);
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

    @GetMapping(value = "/updateTypeFriend/{relationShipID}&{type}")
    public ResponseEntity<HttpResponse> updateTypeFriend(HttpServletRequest request,@PathVariable("relationShipID") String relationShipID,@PathVariable("type") TypeRelationShip type){
        String userID = appUserService.getUserIdByHttpRequest(request);
        return ResponseEntity.ok(appUserService.updateTypeFriend(userID,relationShipID,type));
    }

    @PostMapping(value = "/addGroup")
    public ResponseEntity<HttpResponse> addGroup(HttpServletRequest request,@RequestBody AddGroupRequest addGroupRequest){
        String userID = appUserService.getUserIdByHttpRequest(request);
        System.out.println(addGroupRequest.nameGroup());
        System.out.println(addGroupRequest.urlAvatar());
        for(String id: addGroupRequest.listUserID()){
            System.out.println(id);
        }
        return ResponseEntity.ok(appUserService.addGroup(userID,addGroupRequest));
    }

    @GetMapping(value = "/getAllMediaFiles/{relationShipID}")
    public ResponseEntity<StreamingResponseBody> getAllMediaFiles(HttpServletRequest request,@PathVariable("relationShipID") String relationShipID){
        String userID = appUserService.getUserIdByHttpRequest(request);
        if(userID==null){
            return ResponseEntity.ok(null);
        }
        return ResponseEntity.ok(appUserService.getAllMediaFiles(userID,relationShipID));
    }


}
