package Tip.Connect.model.reponse;

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
public class FriendRResponse {

    @JsonProperty("id")
    private long id;
    @JsonProperty("sender")
    private TinyUser sender;
    @JsonProperty("time_stamp")
    private long timeStamp;

}
