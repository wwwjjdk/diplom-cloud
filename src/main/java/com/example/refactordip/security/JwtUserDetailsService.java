package com.example.refactordip.security;

import com.example.refactordip.exception.UnauthorizedException;
import com.example.refactordip.repository.ClientRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;


@Component
public class JwtUserDetailsService implements UserDetailsService {
    @Autowired
    private ClientRepo clientRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var client = clientRepo.findByName(username);
        if(client == null){
            throw new UnauthorizedException("Unauthorized error");
        }
        return User.builder()
                .username(client.getName())
                .password(client.getPassword())
                .authorities(client.getRole())
                .build();
    }
}
