package Tip.Connect.model.Chat.WsRecord;

import Tip.Connect.model.Chat.RecordType;
import Tip.Connect.model.reponse.FriendRResponse;
import Tip.Connect.model.reponse.FriendShipRespone;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class NotificationChat extends RawChat {

    @JsonProperty("friendRResponse")
    private FriendRResponse friendRResponse;

    @JsonProperty("friendShipRespone")
    private FriendShipRespone friendShipRespone;

    @JsonProperty("actionCode")
    private int actionCode = 0;


    public NotificationChat(FriendRResponse friendRResponse, int actionCode){
        super(RecordType.SYSTEM,friendRResponse.getSender().toString(),new Date().getTime(),false);
        this.friendRResponse = friendRResponse;
        this.actionCode = actionCode;
    }

    public NotificationChat(FriendShipRespone friendShipRespone, int actionCode){
        super(RecordType.SYSTEM,friendShipRespone.getFriend().toString(),new Date().getTime(),false);
        this.friendShipRespone = friendShipRespone;
        this.actionCode = actionCode;
    }


}
