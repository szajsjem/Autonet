package pl.szajsjem.autonet.DB.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.szajsjem.autonet.DB.entity.Token;
import pl.szajsjem.autonet.DB.entity.User;

public interface TokenRepository
        extends JpaRepository<Token, Long> {
    Token findByToken(String token);
    void removeByToken(String token);
}
