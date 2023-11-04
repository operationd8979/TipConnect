package Tip.Connect.model.reponse;

import Tip.Connect.model.TypeFriendShip;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class FriendShipRespone {

    private long id;
    private TinyUser friend;
    private TypeFriendShip type;

}
