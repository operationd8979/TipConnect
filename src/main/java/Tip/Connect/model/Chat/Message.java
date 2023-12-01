package Tip.Connect.model.Chat;

import Tip.Connect.model.Auth.AppUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Message extends Record {


    @Column(length = 500)
    private String content;

    public Message(AppUser user, AppUser user2, String timeStamp, RecordType type, String content){
        super(user,user2,timeStamp,type);
        this.content = content;
    }

}
