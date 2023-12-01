package pl.szajsjem.autonet.DB.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.Set;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Page {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id  ;
    private String url;
    private boolean generated;
    @Column(columnDefinition="TEXT")
    private String errorMessage;

}
