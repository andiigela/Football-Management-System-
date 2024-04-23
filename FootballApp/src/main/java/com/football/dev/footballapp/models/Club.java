package com.football.dev.footballapp.models;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Where(clause = "is_deleted=false")
public class Club extends BaseEntity{

    private String name;
    @OneToOne
    private Stadium stadium;
    private Integer foundedYear;
    private String city;
    private String website;
    @ManyToOne
    private League league;
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;
    @OneToMany
    private List<Player>players;

  //shtoni atribute tjera te nevojshme

    public Club(String name, Integer foundedYear, String city, String website){
        this.name=name;
        this.foundedYear = foundedYear;
        this.city = city;
        this.website = website;


    }
}
