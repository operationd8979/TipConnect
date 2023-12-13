package Tip.Connect.service;

import Tip.Connect.model.Auth.AppUser;
import Tip.Connect.model.Chat.Message;
import Tip.Connect.model.Chat.Record;
import Tip.Connect.model.Chat.RecordType;
import Tip.Connect.model.Chat.WsRecord.MessageChat;
import Tip.Connect.model.Chat.WsRecord.SeenNotification;
import Tip.Connect.model.Relationship.RelationShip;
import Tip.Connect.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final RelationShipService relationShipService;
    private final AppUserService appUserService;

    @Transactional
    public List<String> saveMessage(MessageChat chat){
        AppUser sender = appUserService.loadUserByUserid(chat.getFrom());
        RelationShip relationShip = relationShipService.loadRelationShipById(chat.getTo());
        if(sender==null||relationShip==null){
            return null;
        }
        try{
            Record message = new Message(sender,relationShip,chat.getTimestamp(),chat.getType(),chat.getBody());
            if(chat.getType().equals(RecordType.ENDCALL)){
                message.setSeen(true);
            }
            chatRepository.save(message);
            return relationShip.getListDetailRelationShip().stream().map(d->d.getUser().getId()).filter(s->!s.equals(chat.getFrom())).collect(Collectors.toList());
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    @Transactional
    public List<String> tradeRTC(MessageChat chat){
        AppUser sender = appUserService.loadUserByUserid(chat.getFrom());
        RelationShip relationShip = relationShipService.loadRelationShipById(chat.getTo());
        if(sender==null||relationShip==null){
            return null;
        }
        try{
            return relationShip.getListDetailRelationShip().stream().map(d->d.getUser().getId()).filter(s->!s.equals(chat.getFrom())).collect(Collectors.toList());
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    @Transactional
    public boolean addSeen(SeenNotification seenNotification){
        AppUser sender = appUserService.loadUserByUserid(seenNotification.getFrom());
        RelationShip relationShip = relationShipService.loadRelationShipById(seenNotification.getTo());
        if(sender==null||relationShip==null){
            return false;
        }
        try{
            Record chat = relationShip.getListChat().stream().filter(c->c.getSender()==sender&&c.getTimeStamp().equals(seenNotification.getTimestamp())).findFirst().orElse(null);
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
