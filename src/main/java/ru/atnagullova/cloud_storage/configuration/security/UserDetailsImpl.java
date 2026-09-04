package ru.atnagullova.cloud_storage.configuration.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.atnagullova.cloud_storage.entity.User;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

public class UserDetailsImpl implements UserDetails, Serializable {

    private Long id;
    private String username;
    private String password;

    public UserDetailsImpl(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    public Long getId() {
        return id;
    }
}
