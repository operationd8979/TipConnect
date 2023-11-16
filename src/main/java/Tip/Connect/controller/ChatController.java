package Tip.Connect.controller;


import Tip.Connect.model.Chat.MessageChat;
import Tip.Connect.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final ChatService chatService;

    @MessageMapping("/all")
    @SendTo("/all/messages")
    private MessageChat receivePublicMessage(@Payload MessageChat message, Principal principal){
        System.out.println("nhận tin nhắn from: "+principal.getName());
        message.setFrom(principal.getName());
        return message;
    }

    @MessageMapping("/private")
    private void receivePrivateMessage(@Payload MessageChat chat, Principal principal){
        System.out.println("nhận tin nhắn private from: "+principal.getName() +" to: "+chat.getTo());
        chat.setFrom(principal.getName());
        MessageChat message = chatService.saveMessage(chat);
        if(message!=null){
            simpMessagingTemplate.convertAndSendToUser(chat.getTo(),"/private",message);
        }
    }


}
