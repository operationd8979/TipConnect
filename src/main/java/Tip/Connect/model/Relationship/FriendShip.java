package Tip.Connect.model.Relationship;

import Tip.Connect.model.Auth.AppUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class FriendShip {

    @EmbeddedId
    private FriendShipId friendShipId;


    @Enumerated(EnumType.STRING)
    private TypeFriendShip type;

    @ManyToOne
    private FriendRequest friendRequest;

    public FriendShip(AppUser user,AppUser friend,FriendRequest friendRequest){
        this.friendShipId = new FriendShipId(user, friend);
        this.friendRequest = friendRequest;
        this.type = TypeFriendShip.COMMON;
    }

    public TypeFriendShip getType() {
        return this.type;
    }

    public void setType(TypeFriendShip type){
        this.type = type;
    }



}