package pl.szajsjem.autonet.DB.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import pl.szajsjem.autonet.DB.entity.Token;

public interface TokenRepository
        extends JpaRepository<Token, Long> {
    Token findByToken(String token);
    @Transactional
    void removeByToken(String token);
}
