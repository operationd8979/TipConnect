package Tip.Connect.service;

import Tip.Connect.model.Auth.AppUser;
import Tip.Connect.model.Chat.Message;
import Tip.Connect.model.Chat.Record;
import Tip.Connect.model.Chat.WsRecord.MessageChat;
import Tip.Connect.model.Chat.WsRecord.SeenNotification;
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
            Record message = new Message(sender,receiver,chat.getTimestamp(),chat.getType(),chat.getBody());
            chatRepository.save(message);
            chat.setUser(false);
            chat.setSeen(false);
            return chat;
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    @Transactional
    public MessageChat tradeRTC(MessageChat chat){
        AppUser sender = appUserService.loadUserByUserid(chat.getFrom());
        AppUser receiver = appUserService.loadUserByUserid(chat.getTo());
        if(sender==null||receiver==null){
            return null;
        }
        try{
            chat.setUser(false);
            return chat;
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    @Transactional
    public boolean addSeen(SeenNotification seenNotification){
        AppUser sender = appUserService.loadUserByUserid(seenNotification.getFrom());
        AppUser receiver = appUserService.loadUserByUserid(seenNotification.getTo());
        if(sender==null||receiver==null){
            return false;
        }
        try{
            Record chat = chatRepository.findAll().stream().filter(c->c.getSender()==sender&&c.getReceiver()==receiver
                    &&c.getTimeStamp().equals(seenNotification.getTimestamp())).findFirst().orElse(null);
            if(chat!=null){
                chat.setSeen(true);
                chatRepository.save(chat);
            }
            return true;
        }catch (Exception ex){
            return false;
        }
    }


}
