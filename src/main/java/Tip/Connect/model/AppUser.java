package Tip.Connect.model;

import jakarta.persistence.*;
import lombok.*;
import org.checkerframework.checker.units.qual.Length;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.beans.factory.annotation.Value;
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

    @OneToMany(mappedBy = "user1", cascade = CascadeType.ALL)
    private List<FriendShip> listFrienst;


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
        this.listFrienst = new ArrayList<>();
        this.urlAvatar = "https://firebasestorage.googleapis.com/v0/b/tipconnect-14d4b.appspot.com/o/UserArea%2FurlPic%2Favatar%2FdefaultAvatar.jpg?alt=media&token=a2d3bd79-51f1-453c-a365-4f1a6d57b1da&_gl=1*1vtkw1t*_ga*MTU4MzAyMDEyMS4xNjk4MzI5MTA0*_ga_CW55HF8NVT*MTY5OTA4NjEzMi41LjEuMTY5OTA4NjU2MS4yNi4wLjA.";
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
