package Tip.Connect.model.reponse;

import Tip.Connect.model.Chat.Record;
import Tip.Connect.model.Relationship.TypeFriendShip;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class FriendShipRespone {

    @JsonProperty("id")
    private String id;
    @JsonProperty("friend")
    private TinyUser friend;
    @JsonProperty("type")
    private TypeFriendShip type;
    @JsonProperty("message")
    private Record message;

}
