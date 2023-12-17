package Tip.Connect.model.Chat;

import Tip.Connect.model.Auth.AppUser;
import Tip.Connect.model.Relationship.RelationShip;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "record", indexes = @Index(columnList = "timestamp"))
public abstract class Record {
    @Id
    @GeneratedValue(generator = "custom-id", strategy = GenerationType.IDENTITY)
    @GenericGenerator(name = "custom-id", strategy = "Tip.Connect.security.RecordIdGenerator")
    private String recordID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            nullable = false,
            name = "sender_id"
    )
    private AppUser sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            nullable = false,
            name = "relation_ship_id"
    )
    private RelationShip relationShip;

    private String timeStamp;

    private boolean seen = false;

    private RecordType type;

    public Record(AppUser sender,RelationShip relationShip,String timeStamp,RecordType type){
        this.sender = sender;
        this.relationShip = relationShip;
        this.timeStamp = timeStamp;
        this.type = type;
    }

    public long getTimeStampLong(){
        return Long.parseLong(this.timeStamp);
    }

    public abstract boolean isContainContent(String content);

    public boolean isMediaFile(){
        return this.type.equals(RecordType.PDF)||this.type.equals(RecordType.WORD)||this.type.equals(RecordType.EXCEL)||this.type.equals(RecordType.PHOTO);
    }


}
