package Tip.Connect.utility;

import Tip.Connect.model.Auth.AppUser;
import Tip.Connect.model.Relationship.FriendShip;
import Tip.Connect.model.reponse.StateAimUser;
import Tip.Connect.model.reponse.FriendShipRespone;
import Tip.Connect.model.reponse.TinyUser;
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

    public List<FriendShipRespone> TranslateFriendShipToTiny(List<FriendShip> listFriend){
        List<FriendShipRespone> listFriendResponse = new ArrayList<>();
        for(FriendShip friendShip: listFriend){
            AppUser user = friendShip.getFriend();
            TinyUser tinyUser = new TinyUser(user.getId(),user.getFirstName(),user.getLastName(),user.getFullName(),user.getUrlAvatar());
            tinyUser.setState(StateAimUser.FRIEND);
            FriendShipRespone friendShipRespone = new FriendShipRespone(friendShip.getFriendShipId(),tinyUser,friendShip.getType());
            listFriendResponse.add(friendShipRespone);
        }
        return listFriendResponse;
    }


}
