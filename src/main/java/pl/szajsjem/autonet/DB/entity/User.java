package pl.szajsjem.autonet.DB.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.Set;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id  ;
    private String login ;
    @JsonIgnore
    private String password  ;
    @JsonIgnore
    private byte[] salt;
    private String email ;
    @JsonIgnore
    private boolean admin;
    @JsonIgnore
    @OneToMany
    private Set<Token> tokens;
    @OneToOne
    GenInstructions generationInstructions;
}
