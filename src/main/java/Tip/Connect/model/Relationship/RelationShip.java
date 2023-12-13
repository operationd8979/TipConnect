package Tip.Connect.model.Relationship;

import Tip.Connect.model.Auth.AppUser;
import Tip.Connect.model.Chat.Record;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class RelationShip {

    @Id
    @GeneratedValue(generator = "custom-id", strategy = GenerationType.IDENTITY)
    @GenericGenerator(name = "custom-id", strategy = "Tip.Connect.security.IdGenerator")
    private String relationshipID;

    @OneToMany(mappedBy = "relationShip", cascade = CascadeType.ALL)
    @OrderBy("time_stamp ASC")
    private List<Record> listChat;

    @OneToMany(mappedBy = "detailRelationShipID.relationShip", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetailRelationShip> listDetailRelationShip;


    public List<AppUser> getListAppUser(){
        if(this.listDetailRelationShip==null)
            return new ArrayList<>();
        return this.listDetailRelationShip.stream().map(d->d.getUser()).collect(Collectors.toList());
    }



}