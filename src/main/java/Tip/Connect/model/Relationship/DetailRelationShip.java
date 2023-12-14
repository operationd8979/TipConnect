package Tip.Connect.model.Relationship;

import Tip.Connect.model.Auth.AppUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class DetailRelationShip {

    @EmbeddedId
    private DetailRelationShipID detailRelationShipID;

//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private AppUser user;
//    @ManyToOne
//    @JoinColumn(name = "relation_ship_id")
//    private RelationShip relationShip;

    @Enumerated(EnumType.STRING)
    private TypeRelationShip type;

    public DetailRelationShip(AppUser user, RelationShip relationShip){
//        this.user = user;
//        this.relationShip = relationShip;
        this.detailRelationShipID = new DetailRelationShipID(user,relationShip);
        this.type = TypeRelationShip.COMMON;
    }

    public AppUser getUser(){
        return this.detailRelationShipID.getUser();
    }

    public List<AppUser> getFriends(){
        return this.getRelationShip().getListAppUser().stream().filter(a->!a.equals(this.getUser().getId())).collect(Collectors.toList());
    }

    public RelationShip getRelationShip(){
        return this.detailRelationShipID.getRelationShip();
    }

}
