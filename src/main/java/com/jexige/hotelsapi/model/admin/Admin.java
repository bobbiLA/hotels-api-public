package com.jexige.hotelsapi.model.admin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class Admin implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NaturalId
    private String username;

    @JsonIgnore
    private String password;

    @CollectionTable
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private List<UserRole> roles = Collections.singletonList(UserRole.ADMIN);

    @JsonIgnore
    private boolean isAccountNonExpired = true;

    @JsonIgnore
    private boolean isAccountNonLocked = true;

    @JsonIgnore
    private boolean isCredentialsNonExpired = true;

    @JsonIgnore
    private boolean isEnabled = true;

    ////////////////////////////////////////////////////////////////
    //  CONSTRUCTORS
    ////////////////////////////////////////////////////////////////

    @Builder
    private Admin(String username, String password, List<UserRole> roles) {
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    ////////////////////////////////////////////////////////////////
    //  SECURITY
    ////////////////////////////////////////////////////////////////

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        final List<GrantedAuthority> authorities = new ArrayList<>();
        if (roles != null) {
            roles.forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getValue())));
        }
        return authorities;
    }

}
