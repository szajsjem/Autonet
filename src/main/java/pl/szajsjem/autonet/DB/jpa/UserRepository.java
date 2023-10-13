package pl.szajsjem.autonet.DB.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.szajsjem.autonet.DB.entity.User;

public interface UserRepository
        extends JpaRepository<User, Long> {
    User findByLogin(String login);
    User findByEmail(String email);
}
