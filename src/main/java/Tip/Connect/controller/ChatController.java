package Tip.Connect.controller;


import Tip.Connect.model.Chat.RecordType;
import Tip.Connect.model.Chat.WsRecord.MessageChat;
import Tip.Connect.model.Chat.WsRecord.OnlineNotification;
import Tip.Connect.model.Chat.WsRecord.SeenNotification;
import Tip.Connect.model.Live.LiveShow;
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
        List<LiveShow> liveList = LiveController.liveList;
        String userID = chat.getFrom();
        String body = chat.getBody();
        System.out.println(body);
        switch (body){
            case "live":
                liveList.add(new LiveShow(chat.getFrom()));
                break;
            case "off":
                LiveShow liveOff = liveList.stream().filter(l->l.getHost().equals(chat.getFrom())).findFirst().orElse(null);
                if(liveOff!=null){
                    liveList.remove(liveOff);
                    for(String watcherID : liveOff.getListWatch()){
                        simpMessagingTemplate.convertAndSendToUser(watcherID,"/private",chat);
                    }
                }
                break;
            case "watch":
                LiveShow liveWatch = liveList.stream().filter(l->l.getHost().equals(chat.getTo())).findFirst().orElse(null);
                if(liveWatch!=null){
                    String hostID = liveWatch.addNewWatcher(chat.getFrom());
                    chat.setBody("host");
                    chat.setFrom(hostID);
                    chat.setTo(userID);
                    simpMessagingTemplate.convertAndSendToUser(chat.getTo(),"/private",chat);
                    //count number watch
                    int sizeWatch = liveWatch.getListWatch().size();
                    chat.setType(RecordType.LIVENOTIFY);
                    chat.setBody(Integer.toString(sizeWatch));
                    chat.setFrom(liveWatch.getHost());
                    chat.setTo(liveWatch.getHost());
                    simpMessagingTemplate.convertAndSendToUser(chat.getTo(),"/private",chat);
                }
                break;
            case "off-watch":
                LiveShow liveOffWatch = liveList.stream().filter(l->l.getHost().equals(chat.getTo())).findFirst().orElse(null);
                if(liveOffWatch!=null){
                    int index = liveOffWatch.removeWatcher(userID);
                    String newHostID = liveOffWatch.getHost();
                    if(index!=0){
                        newHostID = liveOffWatch.getListWatch().get(index-1);
                    }
                    if(liveOffWatch.getListWatch().size()-1>=index){
                        String victimID = liveOffWatch.getListWatch().get(index);
                        chat.setBody("host");
                        chat.setFrom(newHostID);
                        chat.setTo(victimID);
                        simpMessagingTemplate.convertAndSendToUser(chat.getTo(),"/private",chat);
                    }
                    //count number watch
                    int sizeWatch = liveOffWatch.getListWatch().size();
                    chat.setType(RecordType.LIVENOTIFY);
                    chat.setBody(Integer.toString(sizeWatch));
                    chat.setFrom(liveOffWatch.getHost());
                    chat.setTo(liveOffWatch.getHost());
                    simpMessagingTemplate.convertAndSendToUser(chat.getTo(),"/private",chat);
                }
                break;
            default:
                LiveShow liveChat = liveList.stream().filter(l->l.getHost().equals(chat.getTo())).findFirst().orElse(null);
                if(liveChat!=null){
                    if(!userID.equals(liveChat.getHost())){
                        String chatFullName = body.split("@")[0];
                        String chatContent = body.split("@")[1];
                        chat.setType(RecordType.LIVECHAT);
                        chat.setFrom(chatFullName);
                        chat.setBody(chatContent);
                        simpMessagingTemplate.convertAndSendToUser(liveChat.getHost(), "/private", chat);
                    }
                    for(String watcherID : liveChat.getListWatch()){
                        if(!watcherID.equals(userID)) {
                            System.out.println(watcherID);
                            System.out.println(userID);
                            String chatFullName = body.split("@")[0];
                            String chatContent = body.split("@")[1];
                            chat.setType(RecordType.LIVECHAT);
                            chat.setFrom(chatFullName);
                            chat.setBody(chatContent);
                            simpMessagingTemplate.convertAndSendToUser(watcherID, "/private", chat);
                        }
                    }
                }
                break;
        }
//        for(LiveShow s : liveList){
//            System.out.println(s);
//        }
    }
}
