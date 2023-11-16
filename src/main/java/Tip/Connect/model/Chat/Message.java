package Tip.Connect.model.Chat;

import Tip.Connect.model.Auth.AppUser;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
public class Message extends Record {

    private String content;

    public Message(AppUser user, AppUser user2, long timeStamp, RecordType type, String content){
        super(user,user2,timeStamp,type);
        this.content = content;
    }

}
