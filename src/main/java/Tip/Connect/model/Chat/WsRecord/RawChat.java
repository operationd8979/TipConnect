package Tip.Connect.model.Chat.WsRecord;

import Tip.Connect.model.Chat.RecordType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RawChat {

    protected RecordType type;
    protected String body;
    protected long timestamp;
    protected boolean seen = false;


}
