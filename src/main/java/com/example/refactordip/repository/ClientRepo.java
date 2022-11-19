package com.example.refactordip.repository;

import com.example.refactordip.model.MyClient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepo extends JpaRepository<MyClient, Long> {
    MyClient findByName(String name);
}
