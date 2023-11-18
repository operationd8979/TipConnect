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

//    @Id
//    @GeneratedValue(generator = "custom-id", strategy = GenerationType.IDENTITY)
//    @GenericGenerator(name = "custom-id", strategy = "Tip.Connect.security.IdGenerator")
//    private String friendShipId;

    @EmbeddedId
    private FriendShipId friendShipId;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id")
//    private AppUser user;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "friend_id")
//    private AppUser friend;

    @Enumerated(EnumType.STRING)
    private TypeFriendShip type;

    @ManyToOne
    private FriendRequest friendRequest;

    public FriendShip(AppUser user,AppUser friend,FriendRequest friendRequest){
//        this.user = user;
//        this.friend = friend;
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