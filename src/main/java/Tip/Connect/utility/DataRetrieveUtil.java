package Tip.Connect.utility;

import Tip.Connect.model.Auth.AppUser;
import Tip.Connect.model.Chat.Message;
import Tip.Connect.model.Chat.Record;
import Tip.Connect.model.Chat.WsRecord.MessageChat;
import Tip.Connect.model.Chat.WsRecord.RawChat;
import Tip.Connect.model.Relationship.*;
import Tip.Connect.model.reponse.FriendRResponse;
import Tip.Connect.model.reponse.RelationShipResponse;
import Tip.Connect.model.reponse.StateAimUser;
import Tip.Connect.model.reponse.TinyUser;
import com.google.common.collect.Iterables;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class DataRetrieveUtil {


    public TinyUser TranslateAppUserToTiny(AppUser user){
        TinyUser tinyUser = new TinyUser(user.getId(),user.getEmail(),user.getFirstName(),user.getLastName(),user.getFullName(),user.getAppUserRole().toString(),user.getEnabled(),user.getUrlAvatar());
        return tinyUser;
    }

    public RelationShipResponse TranslateRelationShipToTiny(DetailRelationShip detailRelationShip, Record message){
        AppUser user = detailRelationShip.getUser();
        RelationShip relationShip = detailRelationShip.getRelationShip();
        List<TinyUser> listFriend = new ArrayList<>();
        System.out.println(relationShip.getListDetailRelationShip());
        for(AppUser friend: relationShip.getListDetailRelationShip().stream().map(d->d.getUser()).collect(Collectors.toList())){
            System.out.println(friend.getId());
            if(friend.getId().equals(user.getId())){
                continue;
            }
            TinyUser tinyUser = new TinyUser(friend.getId(),friend.getFirstName(),friend.getLastName(),friend.getFullName(),friend.getUrlAvatar());
            tinyUser.setState(StateAimUser.FRIEND);
            listFriend.add(tinyUser);
        }
        RawChat chat = null;
        if(message!=null){
            chat = TranslateRecordToTiny(message,message.getSender().getId().equals(user.getId()));
        }
        RelationShipResponse relationShipResponse = null;
        if(relationShip instanceof FriendShip){
            TinyUser friend = listFriend.stream().findFirst().orElse(null);
            relationShipResponse = new RelationShipResponse(relationShip.getRelationshipID(),friend.getFullName(),friend.getUrlAvatar(),listFriend,detailRelationShip.getType(),chat);
        }
        else if (relationShip instanceof GroupShip){
            relationShipResponse = new RelationShipResponse(relationShip.getRelationshipID(),((GroupShip) relationShip).getGroupName(),((GroupShip) relationShip).getUrlPic(),listFriend,detailRelationShip.getType(),chat);
            relationShipResponse.setGroup(true);
        }
        return relationShipResponse;
    }

    public List<RelationShipResponse> TranslateRelationShipToResponse(List<DetailRelationShip> detailRelationShips){
        List<RelationShipResponse> listFriendResponse = new ArrayList<>();
        for(DetailRelationShip detailRelationShip: detailRelationShips){
            Record message = null;
            try{
                message = Iterables.getLast(()->detailRelationShip.getRelationShip().getListChat().iterator());
            }catch (NoSuchElementException ex){}
            listFriendResponse.add(TranslateRelationShipToTiny(detailRelationShip,message));
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
            chat = new MessageChat(record.getType(),((Message) record).getContent(),record.getTimeStamp(),record.isSeen(),record.getSender().getId(),record.getRelationShip().getRelationshipID().toString(),isUser);
        }else{
            chat = new MessageChat(record.getType(),null,record.getTimeStamp(),record.isSeen(),record.getSender().getId(),record.getRelationShip().getRelationshipID(),isUser);
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
