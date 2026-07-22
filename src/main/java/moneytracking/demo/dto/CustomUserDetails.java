package moneytracking.demo.dto;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import moneytracking.demo.entity.UserEntity;

public class CustomUserDetails implements UserDetails {
    private final String email;
    private final String password;
    private final boolean emailVerified;

    public CustomUserDetails(UserEntity user) {
        this.email = user.getEmail();
        this.password = user.getPasswordHash();
        this.emailVerified = user.isEmailVerified();
    }

    public boolean isEmailVerified() {
        return emailVerified;
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
        return email;
    }

}
