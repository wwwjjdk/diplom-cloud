package com.example.refactordip.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.Date;

@Entity
@ToString
@Builder(toBuilder = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "files")
public class MyFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(nullable = false, name = "name")
    String name;
    @Column(nullable = false, name = "size")
    Long size;
    @JsonIgnore
    @Column(nullable = false, name = "path")
    String link;
    @Column(nullable = false, name = "date_of_created")
    Date date;
    @Column(nullable = false, name = "exist")
    @JsonIgnore
    @Enumerated(EnumType.STRING)
    ExistFile exist;

    @JsonIgnore
    @ManyToOne(optional = false)
    MyClient myClient;
}
