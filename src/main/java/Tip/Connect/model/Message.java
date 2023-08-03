package Tip.Connect.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Message {

    private String sender;
    private String receiver;
    private String content;
    private String date;
    private enum status{
        JOIN,
        MESSAGE,
        LEAVE
    };


}
