package pl.szajsjem.autonet.DB.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.szajsjem.autonet.DB.entity.GenInstructions;

public interface GenInstructionsRepository
        extends JpaRepository<GenInstructions, Long> {
}
