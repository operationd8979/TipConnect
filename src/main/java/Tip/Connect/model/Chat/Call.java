package Tip.Connect.model.Chat;

import Tip.Connect.model.Auth.AppUser;
import Tip.Connect.model.Relationship.RelationShip;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Call extends Record {

    private long duration;

    public Call(AppUser user, RelationShip relationShip, String timeStamp, RecordType type, long duration){
        super(user,relationShip,timeStamp,type);
        this.duration = duration;
    }

    @Override
    public boolean isContainContent(String content) {
        return false;
    }
}
