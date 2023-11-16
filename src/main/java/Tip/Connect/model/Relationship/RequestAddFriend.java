package Tip.Connect.model.Relationship;

import Tip.Connect.model.Auth.AppUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class RequestAddFriend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestID;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private AppUser sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private AppUser receiver;

    private boolean enable=false;


    public RequestAddFriend(AppUser sender,AppUser receiver){
        this.sender = sender;
        this.receiver = receiver;
    }

}