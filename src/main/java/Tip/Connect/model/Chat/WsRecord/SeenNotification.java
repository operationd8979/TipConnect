package Tip.Connect.model.Chat.WsRecord;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SeenNotification {
    private String from;
    private String to;
    private String timestamp;
    private String type;
}
