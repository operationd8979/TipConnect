package Tip.Connect.model.Chat.WsRecord;

import Tip.Connect.model.Chat.RecordType;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OnlineNotification {
    private String from;
    private String timestamp;
    private RecordType type;
}
