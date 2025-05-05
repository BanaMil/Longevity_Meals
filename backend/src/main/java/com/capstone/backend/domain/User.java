package com.capstone.backend.domain;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;

@Document(collection = "users") // MongoDB의 "users" 컬렉션에 매핑
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    private String id;
    private String username;
    private String password;
    private LocalDate birthdate;
    private String phone;
    private String address;
}
