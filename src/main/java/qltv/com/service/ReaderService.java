package qltv.com.service;

import qltv.com.entity.BorrowRecord;
import qltv.com.entity.Reader;
import qltv.com.repository.BorrowRecordRepository;
import qltv.com.repository.ReaderRepository;
import qltv.com.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class ReaderService {
    private final ReaderRepository readerRepository;
    private final BorrowRecordRepository borrowRecordRepository;
    private final UserRepository userRepository;

    public ReaderService(ReaderRepository readerRepository,
                         BorrowRecordRepository borrowRecordRepository,
                         UserRepository userRepository) {
        this.readerRepository = readerRepository;
        this.borrowRecordRepository = borrowRecordRepository;
        this.userRepository = userRepository;
    }

    public List<Reader> findAll() { return readerRepository.findAll(); }

    public Optional<Reader> findById(Long id) { return readerRepository.findById(id); }

    public Optional<Reader> findByUserId(Long userId) {
        return readerRepository.findByUserId(userId);
    }

    public Reader save(Reader reader) { return readerRepository.save(reader); }

    @Transactional
    public void delete(Long id) {
        Reader reader = readerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy độc giả!"));

        // Kiểm tra còn phiếu mượn đang hoạt động không
        List<BorrowRecord> activeBorrows = borrowRecordRepository
            .findByReaderId(id).stream()
            .filter(b ->
                b.getStatus() == BorrowRecord.BorrowStatus.PENDING ||
                b.getStatus() == BorrowRecord.BorrowStatus.CONFIRMED ||
                b.getStatus() == BorrowRecord.BorrowStatus.BORROWED
            ).toList();

        if (!activeBorrows.isEmpty()) {
            long pending   = activeBorrows.stream().filter(b -> b.getStatus() == BorrowRecord.BorrowStatus.PENDING).count();
            long confirmed = activeBorrows.stream().filter(b -> b.getStatus() == BorrowRecord.BorrowStatus.CONFIRMED).count();
            long borrowed  = activeBorrows.stream().filter(b -> b.getStatus() == BorrowRecord.BorrowStatus.BORROWED).count();

            StringBuilder msg = new StringBuilder();
            msg.append("Không thể xóa độc giả '")
               .append(reader.getUser().getFullName())
               .append("' vì còn phiếu mượn đang hoạt động: ");
            if (borrowed > 0)  msg.append(borrowed).append(" sách đang mượn, ");
            if (confirmed > 0) msg.append(confirmed).append(" chờ nhận sách, ");
            if (pending > 0)   msg.append(pending).append(" chờ duyệt, ");

            String result = msg.toString().replaceAll(",\\s*$", "");
            result += ". Vui lòng xử lý hết phiếu mượn trước khi xóa!";
            throw new RuntimeException(result);
        }

        // Lưu user_id trước khi xóa reader
        Long userId = reader.getUser().getId();

        // 1. Xóa toàn bộ lịch sử mượn
        List<BorrowRecord> history = borrowRecordRepository.findByReaderId(id);
        borrowRecordRepository.deleteAll(history);

        // 2. Xóa reader
        readerRepository.deleteById(id);

        // 3. Xóa luôn tài khoản user
        userRepository.deleteById(userId);
    }

    public long count() { return readerRepository.count(); }
}