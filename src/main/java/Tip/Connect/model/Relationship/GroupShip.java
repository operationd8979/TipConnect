package Tip.Connect.model.Relationship;

import Tip.Connect.model.Auth.AppUser;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class GroupShip extends RelationShip {
    private String groupName;
    private String urlPic;
    private Set<String> listAdminID;

    public GroupShip(String urlPic,String groupName,String userID){
        super();
        this.groupName = groupName;
        this.urlPic = urlPic;
        this.listAdminID = new HashSet<>();
        listAdminID.add(userID);
    }

}
