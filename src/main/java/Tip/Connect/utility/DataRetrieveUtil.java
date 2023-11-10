package Tip.Connect.utility;

import Tip.Connect.model.AppUser;
import Tip.Connect.model.FriendShip;
import Tip.Connect.model.reponse.FriendShipRespone;
import Tip.Connect.model.reponse.TinyUser;
import Tip.Connect.service.AppUserService;
import Tip.Connect.service.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
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
            FriendShipRespone friendShipRespone = new FriendShipRespone(friendShip.getFriendShipId(),tinyUser,friendShip.getType());
            listFriendResponse.add(friendShipRespone);
        }
        return listFriendResponse;
    }


}
