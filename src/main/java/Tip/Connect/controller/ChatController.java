package Tip.Connect.controller;


import Tip.Connect.model.Chat.RecordType;
import Tip.Connect.model.Chat.WsRecord.MessageChat;
import Tip.Connect.model.Chat.WsRecord.OnlineNotification;
import Tip.Connect.model.Chat.WsRecord.SeenNotification;
import Tip.Connect.service.AppUserService;
import Tip.Connect.service.ChatService;
import Tip.Connect.websocket.config.PrincipalUser;
import Tip.Connect.websocket.config.UserInterceptor;
import com.google.common.collect.Iterables;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.concurrent.ConcurrentHashMap;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final AppUserService appUserService;

    private final SimpMessagingTemplate simpMessagingTemplate;


    @MessageMapping("/all")
    @SendTo("/all/messages")
    private MessageChat receivePublicMessage(@Payload MessageChat message, Principal principal){
        message.setFrom(principal.getName());
        return message;
    }

    @MessageMapping("/private")
    private void receivePrivateMessage(@Payload MessageChat chat, Principal principal){
        chat.setFrom(principal.getName());
        MessageChat message = chatService.saveMessage(chat);
        if(message!=null){
            if(message.getType().equals(RecordType.ENDCALL)){
                simpMessagingTemplate.convertAndSendToUser(message.getTo(),"/private",message);
                message.setUser(true);
                simpMessagingTemplate.convertAndSendToUser(message.getFrom(),"/private",message);
            }
            else{
                simpMessagingTemplate.convertAndSendToUser(message.getTo(),"/private",message);
            }
        }
    }

    @MessageMapping("/trade")
    private void tradeRTC(@Payload MessageChat chat){
        MessageChat message = chatService.tradeRTC(chat);
        if(message!=null){
            simpMessagingTemplate.convertAndSendToUser(chat.getTo(),"/private",message);
        }
    }


    @MessageMapping("/seen")
    private void addSeen(@Payload SeenNotification seenNotification, Principal principal){
        seenNotification.setTo(principal.getName());
        if(chatService.addSeen(seenNotification)){
            simpMessagingTemplate.convertAndSendToUser(seenNotification.getFrom(),"/private",seenNotification);
        }
    }

    @MessageMapping("/online")
    private void notifyOnline(@Payload OnlineNotification message, Principal principal){
        String userID = principal.getName();
        message.setFrom(userID);
        Map<String, PrincipalUser> map = UserInterceptor.loggedInUsers;
        List<String> listFriendID = appUserService.getListFriendID(userID);
        for(String friendID : listFriendID){
            if(map.containsKey(friendID)){
                simpMessagingTemplate.convertAndSendToUser(friendID,"/private",message);
            }
        }
    }

    @MessageMapping("/live")
    private void liveAction(@Payload MessageChat chat, Principal principal){
        List<String> liveList = LiveController.liveList;
        List<String> watchList = LiveController.watchList;
        System.out.println(chat.getBody());
        if(chat.getBody().equals("live")){
            liveList.add(chat.getFrom());
        }
        if(chat.getBody().equals("off")){
            liveList.remove(chat.getFrom());
        }
        if(chat.getBody().equals("off-watch")){
            String userID = chat.getFrom();
            watchList.remove(userID);
        }
        if(chat.getBody().equals("watch")){
            String hostID = chat.getTo();
            String userID = chat.getFrom();
            if(watchList.size()>0){
                hostID = Iterables.getLast(() -> watchList.iterator());
            }
            watchList.add(chat.getFrom());
            chat.setBody("host");
            chat.setFrom(hostID);
            chat.setTo(userID);
            simpMessagingTemplate.convertAndSendToUser(chat.getTo(),"/private",chat);
        }
        for(String s : liveList){
            System.out.println(s);
        }
    }


}
