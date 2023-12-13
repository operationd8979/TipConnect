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
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.security.Principal;
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
        List<String> listFriendID = chatService.saveMessage(chat);
        chat.setSeen(false);
        chat.setUser(false);
        if(chat.getType().equals(RecordType.ENDCALL)){
            chat.setSeen(true);
            for(String friendID:listFriendID){
                simpMessagingTemplate.convertAndSendToUser(friendID,"/private",chat);
            }
            chat.setUser(true);
            simpMessagingTemplate.convertAndSendToUser(chat.getFrom(),"/private",chat);
        }
        else{
            for(String friendID:listFriendID){
                simpMessagingTemplate.convertAndSendToUser(friendID,"/private",chat);
            }
        }
    }

    @MessageMapping("/trade")
    private void tradeRTC(@Payload MessageChat chat){
        List<String> listFriendID = chatService.tradeRTC(chat);
        chat.setUser(false);
        for(String friendID:listFriendID){
            simpMessagingTemplate.convertAndSendToUser(friendID,"/private",chat);
        }
    }


    @MessageMapping("/seen")
    private void addSeen(@Payload SeenNotification seenNotification, Principal principal){
        String userID = principal.getName();
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

    @MessageMapping("/tradeLive")
    private void tradeLive(@Payload MessageChat chat){
        chat.setUser(false);
        if(chat!=null){
            simpMessagingTemplate.convertAndSendToUser(chat.getTo(),"/private",chat);
        }
    }

    @MessageMapping("/live")
    private void liveAction(@Payload MessageChat chat, Principal principal){
        List<String> liveList = LiveController.liveList;
        List<String> watchList = LiveController.watchList;
        System.out.println(chat.getBody());
        if(chat.getBody().equals("live")){
            liveList.add(chat.getFrom());
            System.out.println("add live");
        }
        if(chat.getBody().equals("off")){
            liveList.remove(chat.getFrom());
            System.out.println("off live");
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
