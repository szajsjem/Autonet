package pl.szajsjem.autonet.DB.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.szajsjem.autonet.DB.entity.Page;

public interface PageRepository
        extends JpaRepository<Page, Long> {
    Page findByUrl(String url);
}
