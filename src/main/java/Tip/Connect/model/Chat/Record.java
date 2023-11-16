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
public class Record {
    @Id
    @GeneratedValue(generator = "custom-id", strategy = GenerationType.IDENTITY)
    @GenericGenerator(name = "custom-id", strategy = "Tip.Connect.security.RecordIdGenerator")
    private String recordID;

    @ManyToOne
    @JoinColumn(
            nullable = false,
            name = "sender"
    )
    private AppUser sender;

    @ManyToOne
    @JoinColumn(
            nullable = false,
            name = "receiver"
    )
    private AppUser receiver;

    private long timestamp;

    private boolean seen = false;

    private RecordType type;

    public Record(AppUser sender,AppUser receiver,long timestamp,RecordType type){
        this.sender = sender;
        this.receiver = receiver;
        this.timestamp = timestamp;
        this.type = type;
    }



}