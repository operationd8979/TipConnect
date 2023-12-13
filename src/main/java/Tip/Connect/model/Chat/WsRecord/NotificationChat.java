package Tip.Connect.model.Chat.WsRecord;

import Tip.Connect.model.Chat.RecordType;
import Tip.Connect.model.reponse.FriendRResponse;
import Tip.Connect.model.reponse.RelationShipResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class NotificationChat extends RawChat {

    @JsonProperty("friendRResponse")
    private FriendRResponse friendRResponse;

    @JsonProperty("relationShipResponse")
    private RelationShipResponse relationShipResponse;

    @JsonProperty("actionCode")
    private int actionCode = 0;


    public NotificationChat(FriendRResponse friendRResponse, int actionCode){
        super(RecordType.SYSTEM,Long.toString(new Date().getTime()),false);
        this.friendRResponse = friendRResponse;
        this.actionCode = actionCode;
    }

    public NotificationChat(RelationShipResponse relationShipResponse, int actionCode){
        super(RecordType.SYSTEM,Long.toString(new Date().getTime()),false);
        this.relationShipResponse = relationShipResponse;
        this.actionCode = actionCode;
    }


}
