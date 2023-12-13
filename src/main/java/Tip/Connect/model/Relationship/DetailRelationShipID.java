package Tip.Connect.model.Relationship;

import Tip.Connect.model.Auth.AppUser;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class DetailRelationShipID implements Serializable {

    @ManyToOne
    @JoinColumn(name = "user_id")
    private AppUser user;
    @ManyToOne
    @JoinColumn(name = "relation_ship_id")
    private RelationShip relationShip;

    @Override
    public String toString(){
        return user.getId()+relationShip.getRelationshipID();
    }

}
