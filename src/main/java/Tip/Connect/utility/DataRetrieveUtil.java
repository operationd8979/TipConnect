package Tip.Connect.utility;

import Tip.Connect.model.Auth.AppUser;
import Tip.Connect.model.Chat.Message;
import Tip.Connect.model.Chat.Record;
import Tip.Connect.model.Chat.WsRecord.MessageChat;
import Tip.Connect.model.Chat.WsRecord.RawChat;
import Tip.Connect.model.Relationship.FriendRequest;
import Tip.Connect.model.Relationship.FriendShip;
import Tip.Connect.model.reponse.FriendRResponse;
import Tip.Connect.model.reponse.StateAimUser;
import Tip.Connect.model.reponse.FriendShipRespone;
import Tip.Connect.model.reponse.TinyUser;
import com.google.common.collect.Iterables;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Component
@AllArgsConstructor
public class DataRetrieveUtil {


    public TinyUser TranslateAppUserToTiny(AppUser user){
        TinyUser tinyUser = new TinyUser(user.getId(),user.getEmail(),user.getFirstName(),user.getLastName(),user.getFullName(),user.getAppUserRole().toString(),user.getEnabled(),user.getUrlAvatar());
        return tinyUser;
    }

    public FriendShipRespone TranslateFriendShipToTiny(FriendShip friendShip,Record message){
        AppUser user = friendShip.getFriendShipId().getFriend();
        TinyUser tinyUser = new TinyUser(user.getId(),user.getFirstName(),user.getLastName(),user.getFullName(),user.getUrlAvatar());
        tinyUser.setState(StateAimUser.FRIEND);
        RawChat chat = null;
        if(message!=null){
            chat = TranslateRecordToTiny(message,message.getSender().getId().equals(friendShip.getFriendShipId().getUser().getId()));
        }
        FriendShipRespone friendShipRespone = new FriendShipRespone(friendShip.getFriendShipId().toString(),tinyUser,friendShip.getType(),chat);
        return friendShipRespone;
    }

    public List<FriendShipRespone> TranslateFriendShipToResponse(List<FriendShip> listFriend,AppUser user){
        List<FriendShipRespone> listFriendResponse = new ArrayList<>();
        for(FriendShip friendShip: listFriend){
            Record message = null;
            Record messageFriend = null;
            Record messageUser = null;

            try{
                messageFriend = Iterables.getLast(() -> user.getListChat().stream().filter(c->c.getSender().getId().equals(friendShip.getFriendShipId().getFriend().getId())).iterator());
            }catch(NoSuchElementException ex){}
            try{
                messageUser = Iterables.getLast(() -> user.getListMyChat().stream().filter(c->c.getReceiver().getId().equals(friendShip.getFriendShipId().getFriend().getId())).iterator());
            }catch(NoSuchElementException ex){}

            if(messageFriend!=null&&messageUser!=null){
                message = Long.parseLong(messageFriend.getTimeStamp()) > Long.parseLong(messageUser.getTimeStamp())?messageFriend:messageUser;
            }
            else{
                message = messageUser!=null?messageUser:messageFriend!=null?messageFriend:null;
            }

            listFriendResponse.add(TranslateFriendShipToTiny(friendShip,message));
        }
        return listFriendResponse;
    }

    public FriendRResponse TranslateFriendRequestToTiny(FriendRequest friendRequest){
        AppUser user = friendRequest.getSender();
        TinyUser tinyUser = new TinyUser(user.getId(),user.getFirstName(),user.getLastName(),user.getFullName(),user.getUrlAvatar());
        tinyUser.setState(StateAimUser.ONWAIT);
        FriendRResponse friendRResponse = new FriendRResponse(friendRequest.getRequestID(),tinyUser,friendRequest.getTimeStamp());
        return friendRResponse;
    }

    public List<FriendRResponse> TranslateFriendRequestToResponse(List<FriendRequest> friendRequests){
        List<FriendRResponse> listFRResponse = new ArrayList<>();
        for(FriendRequest friendRequest: friendRequests){
            listFRResponse.add(TranslateFriendRequestToTiny(friendRequest));
        }
        return listFRResponse;
    }

    public RawChat TranslateRecordToTiny(Record record,boolean isUser){
        RawChat chat = null;
        if(record instanceof Message){
            chat = new MessageChat(record.getType(),((Message) record).getContent(),record.getTimeStamp(),record.isSeen(),record.getSender().getId(),record.getReceiver().getId(),isUser);
        }else{
            chat = new MessageChat(record.getType(),null,record.getTimeStamp(),record.isSeen(),record.getSender().getId(),record.getReceiver().getId(),isUser);
        }
        return chat;
    }
    public List<RawChat> TranslateRecordToResponse(List<Record> records,String userID){
        List<RawChat> rawChats = new ArrayList<>();
        for(Record record: records){
            rawChats.add(TranslateRecordToTiny(record,record.getSender().getId().equals(userID)));
        }
        return rawChats;
    }


}
