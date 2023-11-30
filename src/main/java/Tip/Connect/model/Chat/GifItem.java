package Tip.Connect.model.Chat;

import Tip.Connect.model.Auth.AppUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class GifItem {

    @Id
    @GeneratedValue(generator = "custom-id", strategy = GenerationType.IDENTITY)
    private Integer gifID;

    private String gifName;

    @Column(length = 500)
    private String url;

}
