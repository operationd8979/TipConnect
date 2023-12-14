package Tip.Connect.model.Relationship;


import Tip.Connect.model.Auth.AppUser;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class FriendShip extends RelationShip {

    @ManyToOne
    private FriendRequest friendRequest;

    public FriendShip(FriendRequest friendRequest){
        super();
        this.friendRequest = friendRequest;
    }


}
