package Tip.Connect.model.Chat.WsRecord;

import Tip.Connect.model.Chat.RecordType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class RawChat {

    protected RecordType type;
    protected String timestamp;
    protected boolean seen = false;

}
