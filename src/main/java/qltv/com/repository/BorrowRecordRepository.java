package qltv.com.repository;

import qltv.com.entity.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    List<BorrowRecord> findByReaderId(Long readerId);
    List<BorrowRecord> findByStatus(BorrowRecord.BorrowStatus status);
    List<BorrowRecord> findByReaderIdAndStatus(Long readerId, BorrowRecord.BorrowStatus status);

    @Query("SELECT COUNT(b) FROM BorrowRecord b WHERE b.status = 'BORROWED' AND b.dueDate < :today")
    long countOverdue(LocalDate today);

    @Query("SELECT COUNT(b) FROM BorrowRecord b WHERE b.borrowDate >= :startDate")
    long countBorrowsThisMonth(LocalDate startDate);
}