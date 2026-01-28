package com.hostpitami.security.auth;

import com.hostpitami.domain.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository users;

    public UserDetailsServiceImpl(UserRepository users) {
        this.users = users;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var u = users.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new AppUserDetails(u);
    }
}
