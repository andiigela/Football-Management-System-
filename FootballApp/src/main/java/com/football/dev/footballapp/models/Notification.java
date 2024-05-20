package com.football.dev.footballapp.models;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@Getter
@Setter
@Document(collection = "notifications")
public class Notification {
    @Id
    private String id;
    private Long userId;
    private String description;
}