package qltv.com.repository;

import qltv.com.entity.Reader;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ReaderRepository extends JpaRepository<Reader, Long> {
    Optional<Reader> findByUserId(Long userId);
    Optional<Reader> findByReaderCode(String readerCode);
}