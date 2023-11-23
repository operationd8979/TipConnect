package Tip.Connect.model.Chat;

import Tip.Connect.model.Auth.AppUser;
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
public class Record {
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
            name = "receiver_id"
    )
    private AppUser receiver;

    private long timeStamp;

    private boolean seen = false;

    private RecordType type;

    public Record(AppUser sender,AppUser receiver,long timeStamp,RecordType type){
        this.sender = sender;
        this.receiver = receiver;
        this.timeStamp = timeStamp;
        this.type = type;
    }


}
