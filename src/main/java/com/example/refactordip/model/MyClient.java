package com.example.refactordip.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "clients")
public class MyClient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, name = "name")
    String name;

    @Column(nullable = false, name = "password")
    String password;

    @Column(nullable = false, name = "date")
    Date date;

    @Column(nullable = false, name = "role")
    String role;
}
