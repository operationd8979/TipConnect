package Tip.Connect.utility;

import Tip.Connect.model.Auth.AppUser;
import Tip.Connect.model.Chat.Record;
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
        FriendShipRespone friendShipRespone = new FriendShipRespone(friendShip.getFriendShipId().toString(),tinyUser,friendShip.getType(),message);
        return friendShipRespone;
    }

    public List<FriendShipRespone> TranslateFriendShipToResponse(List<FriendShip> listFriend,AppUser user){
        List<FriendShipRespone> listFriendResponse = new ArrayList<>();
        for(FriendShip friendShip: listFriend){
            Record message = Iterables.getLast(() -> user.getNewChats().stream().filter(c->c.getSender().getId().equals(friendShip.getFriendShipId().getFriend().getId())).iterator());
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


}
