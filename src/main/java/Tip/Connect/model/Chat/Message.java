package Tip.Connect.model.Chat;

import Tip.Connect.model.Auth.AppUser;
import Tip.Connect.model.Relationship.RelationShip;
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

    public Message(AppUser user, RelationShip relationShip, String timeStamp, RecordType type, String content){
        super(user,relationShip,timeStamp,type);
        this.content = content;
    }

    @Override
    public boolean isContainContent(String content) {
        return this.content.contains(content.trim().strip());
    }

}
