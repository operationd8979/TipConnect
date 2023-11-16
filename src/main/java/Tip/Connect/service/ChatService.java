package Tip.Connect.service;

import Tip.Connect.model.Auth.AppUser;
import Tip.Connect.model.Chat.MessageChat;
import Tip.Connect.model.Chat.Message;
import Tip.Connect.model.Chat.Record;
import Tip.Connect.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final AppUserService appUserService;

    @Transactional
    public MessageChat saveMessage(MessageChat chat){
        AppUser sender = appUserService.loadUserByUserid(chat.getFrom());
        AppUser receiver = appUserService.loadUserByUserid(chat.getTo());
        if(sender==null||receiver==null){
            return null;
        }
        try{
            Record message = new Message(sender,receiver,new Date().getTime(),chat.getType(),chat.getBody());
            chatRepository.save(message);
            chat.setTimestamp(message.getTimestamp());
            return chat;
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

}