package qltv.com.service;

import qltv.com.entity.*;
import qltv.com.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BorrowService {
    private final BorrowRecordRepository borrowRecordRepository;
    private final BookRepository bookRepository;
    private final ReaderRepository readerRepository;

    public BorrowService(BorrowRecordRepository borrowRecordRepository,
                         BookRepository bookRepository,
                         ReaderRepository readerRepository) {
        this.borrowRecordRepository = borrowRecordRepository;
        this.bookRepository = bookRepository;
        this.readerRepository = readerRepository;
    }

    public List<BorrowRecord> findAll() { return borrowRecordRepository.findAll(); }
    public Optional<BorrowRecord> findById(Long id) { return borrowRecordRepository.findById(id); }
    public List<BorrowRecord> findByReader(Long readerId) {
        return borrowRecordRepository.findByReaderId(readerId);
    }
    public List<BorrowRecord> findPending() {
        return borrowRecordRepository.findByStatus(BorrowRecord.BorrowStatus.PENDING);
    }

    @Transactional
    public BorrowRecord requestBorrow(Long bookId, Long userId) {
        Reader reader = readerRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Khong tim thay the doc gia"));
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new RuntimeException("Khong tim thay sach"));
        if (book.getAvailableCopies() <= 0)
            throw new RuntimeException("Sach da het, khong the dang ky muon");

        BorrowRecord record = new BorrowRecord(
            reader, book, LocalDate.now(),
            LocalDate.now().plusDays(14),
            BorrowRecord.BorrowStatus.PENDING
        );
        return borrowRecordRepository.save(record);
    }

    @Transactional
    public BorrowRecord approveBorrow(Long recordId, Long staffId) {
        BorrowRecord record = borrowRecordRepository.findById(recordId)
            .orElseThrow(() -> new RuntimeException("Khong tim thay phieu muon"));
        record.setReceiptCode("PM" + String.format("%06d", recordId));
        record.setBorrowFee(70000);
        record.setStatus(BorrowRecord.BorrowStatus.CONFIRMED);
        return borrowRecordRepository.save(record);
    }

    @Transactional
    public BorrowRecord confirmPayment(Long recordId, Long staffId) {
        BorrowRecord record = borrowRecordRepository.findById(recordId)
            .orElseThrow(() -> new RuntimeException("Khong tim thay phieu muon"));
        Book book = record.getBook();
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        if (book.getAvailableCopies() == 0) book.setStatus(Book.BookStatus.OUT_OF_STOCK);
        bookRepository.save(book);
        record.setStatus(BorrowRecord.BorrowStatus.BORROWED);
        return borrowRecordRepository.save(record);
    }

    @Transactional
    public BorrowRecord returnBook(Long recordId) {
        BorrowRecord record = borrowRecordRepository.findById(recordId)
            .orElseThrow(() -> new RuntimeException("Khong tim thay phieu muon"));
        Book book = record.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        book.setStatus(Book.BookStatus.AVAILABLE);
        bookRepository.save(book);
        record.setReturnDate(LocalDate.now());
        record.setStatus(BorrowRecord.BorrowStatus.RETURNED);
        return borrowRecordRepository.save(record);
    }

    @Transactional
    public void rejectBorrow(Long recordId) {
        BorrowRecord record = borrowRecordRepository.findById(recordId)
            .orElseThrow(() -> new RuntimeException("Khong tim thay phieu muon"));
        record.setStatus(BorrowRecord.BorrowStatus.REJECTED);
        borrowRecordRepository.save(record);
    }

    // === THÊM MỚI: Gia hạn sách ===
    @Transactional
    public BorrowRecord renewBorrow(Long recordId, Long userId) {
        BorrowRecord record = borrowRecordRepository.findById(recordId)
            .orElseThrow(() -> new RuntimeException("Khong tim thay phieu muon"));

        // Kiểm tra có phải của user này không
        if (!record.getReader().getUser().getId().equals(userId)) {
            throw new RuntimeException("Ban khong co quyen gia han phieu nay");
        }
        // Chỉ gia hạn khi đang mượn
        if (record.getStatus() != BorrowRecord.BorrowStatus.BORROWED) {
            throw new RuntimeException("Chi co the gia han khi dang muon sach");
        }
        // Tối đa 2 lần
        if (record.getRenewCount() >= 2) {
            throw new RuntimeException("Da gia han toi da 2 lan, khong the gia han them");
        }
        // Gia hạn thêm 7 ngày từ ngày hiện tại
        record.setDueDate(record.getDueDate().plusDays(7));
        record.setRenewCount(record.getRenewCount() + 1);
        record.setLastRenewDate(LocalDate.now());

        return borrowRecordRepository.save(record);
    }

    public long countPending() {
        return borrowRecordRepository.findByStatus(BorrowRecord.BorrowStatus.PENDING).size();
    }

    public long countOverdue() {
        return borrowRecordRepository.countOverdue(LocalDate.now());
    }
}