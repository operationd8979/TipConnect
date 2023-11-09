package Tip.Connect.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Entity
public class FriendShip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Long friendShipId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private final AppUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id")
    private final AppUser friend;

    @Enumerated(EnumType.STRING)
    private TypeFriendShip type;

    public TypeFriendShip getType() {
        return this.type;
    }

    public void setType(TypeFriendShip type){
        this.type = type;
    }

}