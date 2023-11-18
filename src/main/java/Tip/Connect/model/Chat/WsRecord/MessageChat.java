package Tip.Connect.model.Chat.WsRecord;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MessageChat extends RawChat {

    private String from;
    private String to;

}
