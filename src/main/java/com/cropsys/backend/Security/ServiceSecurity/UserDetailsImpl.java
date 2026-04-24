package com.cropsys.backend.Security.ServiceSecurity;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import com.cropsys.backend.model.Role; // Ensure Role is imported from the correct package

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.cropsys.backend.model.State;
import com.cropsys.backend.model.User;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private Long id;
    private String name;
    private String email;
    private String password;
    private State state;
    private String phoneNumber;

    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(
            Long id,
            String name,
            String email,
            String password,
            State state,
            String phoneNumber,
            Collection<? extends GrantedAuthority> authorities
    ) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.state = state;
        this.phoneNumber = phoneNumber;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(User user) {

        List<SimpleGrantedAuthority> authorities =
                (user.getRoles() == null ? List.of() : user.getRoles())
                        .stream()
                        .filter(Objects::nonNull)
                        .map(role -> {
                            if (role instanceof Role && ((Role) role).getRoleName() != null) {
                                return new SimpleGrantedAuthority(
                                        ((Role) role).getRoleName().name()
                                );
                            }
                            return null;
                        })
                        .filter(Objects::nonNull)
                        .toList();
    
        return new UserDetailsImpl(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                user.getState(),
                user.getPhoneNumber(),
                authorities
        );
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserDetailsImpl)) return false;
        UserDetailsImpl that = (UserDetailsImpl) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
