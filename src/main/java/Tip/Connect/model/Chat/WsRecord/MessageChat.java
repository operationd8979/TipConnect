package Tip.Connect.model.Chat.WsRecord;

import Tip.Connect.model.Auth.AppUser;
import Tip.Connect.model.Chat.RecordType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MessageChat extends RawChat {

    private String from;
    private String to;

    private boolean isUser = false;

    public MessageChat(RecordType type, String body,long timestamp,boolean seen, String from, String to, boolean isUser){
        super(type,body,timestamp,seen);
        this.from = from;
        this.to = to;
        this.isUser = isUser;
    }

}
