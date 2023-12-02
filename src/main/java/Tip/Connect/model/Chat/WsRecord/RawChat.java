package Tip.Connect.model.Chat.WsRecord;

import Tip.Connect.model.Chat.RecordType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RawChat {

    protected RecordType type;
    protected String timestamp;
    protected boolean seen = false;
    private String offset;

    public RawChat(RecordType type,String timestamp,boolean seen){
        this.type = type;
        this.timestamp = timestamp;
        this.seen = seen;
    }

}
