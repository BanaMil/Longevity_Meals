package com.capstone.backend.domain;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;

@Document(collection = "diseases")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Disease {
    @Id
    private String id;
    private String name;
    private String aliases;
}
