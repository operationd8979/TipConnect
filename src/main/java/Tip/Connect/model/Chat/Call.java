package Tip.Connect.model.Chat;

import Tip.Connect.model.Auth.AppUser;
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

    public Call(AppUser user, AppUser user2, String timeStamp, RecordType type, long duration){
        super(user,user2,timeStamp,type);
        this.duration = duration;
    }

}
