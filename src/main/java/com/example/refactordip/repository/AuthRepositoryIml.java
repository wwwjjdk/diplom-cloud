package com.example.refactordip.repository;

import com.example.refactordip.model.MyClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class AuthRepositoryIml implements CommandLineRunner {
    //active session
    private ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();

    @Autowired
    private ClientRepo clientRepo;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        clientRepo.save(MyClient.builder()
                .date(new Date())
                .name("user1@yandex.ru")
                .password(passwordEncoder.encode("password1"))
                .role("ROLE_CLIENT")
                .build());
        clientRepo.save(MyClient.builder()
                .date(new Date())
                .name("user2@yandex.ru")
                .password(passwordEncoder.encode("password2"))
                .role("ROLE_CLIENT").build());

    }

    public void putMap(String token, String username){
        map.put(token,username);
    }

    public String getMap(String token){
        return map.get(token);
    }

    public void deleteMap(String token){
        map.remove(token);
    }

}
