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

    public NotificationChat(FriendRResponse friendRResponse){
        this.type = RecordType.SYSTEM;
        this.body = friendRResponse.getSender().getFullName()+" gửi lời mời kết bạn";
        this.timestamp = new Date().getTime();
        this.friendRResponse = friendRResponse;
    }

    public NotificationChat(FriendShipRespone friendShipRespone){
        this.type = RecordType.SYSTEM;
        this.body = friendShipRespone.getFriend()+" đã thêm vào danh sách bạn bè";
        this.timestamp = new Date().getTime();
        this.friendShipRespone = friendShipRespone;
    }


}
