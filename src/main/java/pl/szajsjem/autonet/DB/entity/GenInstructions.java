package pl.szajsjem.autonet.DB.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.Set;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class GenInstructions {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id ;
    @Column(columnDefinition="TEXT")
    private String selectedModel;
    @Column(columnDefinition="TEXT")
    private String pageGenSystemText;
    @Column(columnDefinition="TEXT")
    private String pageGenUserText;
}
