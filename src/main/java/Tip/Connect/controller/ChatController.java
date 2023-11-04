package Tip.Connect.controller;


import Tip.Connect.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/all") //app/message
    @SendTo("/all")
    private Message receivePublicMessage(@Payload Message message){
        return message;
    }

    @MessageMapping("/private")
    private Message receivePrivateMessage(@Payload Message message, Principal principal){
        simpMessagingTemplate.convertAndSendToUser(message.getReceiver(),"/private",message);
        return message;
    }


}
