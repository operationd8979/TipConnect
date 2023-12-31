package Tip.Connect.model.Auth;

import Tip.Connect.model.Chat.Record;
import Tip.Connect.model.Relationship.DetailRelationShip;
import Tip.Connect.model.Relationship.FriendRequest;
import Tip.Connect.model.Relationship.FriendShip;
import Tip.Connect.model.Relationship.RelationShip;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Entity
public class AppUser implements UserDetails {

    @Id
    @GeneratedValue(generator = "custom-id", strategy = GenerationType.IDENTITY)
    @GenericGenerator(name = "custom-id", strategy = "Tip.Connect.security.UserIdGenerator")
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    @Column(length = 500)
    private String urlAvatar;
    @Enumerated(EnumType.STRING)
    private AppUserRole appUserRole;
    private boolean looked = false;
    private boolean enabled = false;

    @OneToMany(mappedBy = "detailRelationShipID.user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetailRelationShip> detailRelationShipList;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    @OrderBy("time_stamp ASC")
    private List<FriendRequest> sentFriendRequests;
    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL)
    @OrderBy("time_stamp ASC")
    private List<FriendRequest> receivedFriendRequests;

//    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
//    @OrderBy("time_stamp ASC")
//    private List<Record> listMyChat;
//    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL)
//    @OrderBy("time_stamp ASC")
//    private List<Record> listChat;


    public AppUser(String firstName,
                   String lastName,
                   String email,
                   String password,
                   AppUserRole appUserRole) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.appUserRole = appUserRole;
        this.detailRelationShipList = new ArrayList<>();
        this.urlAvatar = "https://firebasestorage.googleapis.com/v0/b/tipconnect-14d4b.appspot.com/o/Default%2FdefaultAvatar.jpg?alt=media&token=a0a33d34-e4c4-4ed0-8b52-6da79b7b048a";
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority =
                new SimpleGrantedAuthority(appUserRole.name());
        return Collections.singleton(authority);
    }

    @Override
    public String getPassword() { return password; }

    @Override
    public String getUsername() { return email; }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return !looked; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

    public boolean getEnabled() {
        return this.enabled;
    }

    public String getFullName(){
        return this.firstName+" "+this.lastName;
    }

}
