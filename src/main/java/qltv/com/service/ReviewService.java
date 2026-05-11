package qltv.com.service;

import qltv.com.entity.*;
import qltv.com.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final BorrowRecordRepository borrowRecordRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         BookRepository bookRepository,
                         BorrowRecordRepository borrowRecordRepository) {
        this.reviewRepository = reviewRepository;
        this.bookRepository = bookRepository;
        this.borrowRecordRepository = borrowRecordRepository;
    }

    public List<Review> findByBook(Long bookId) {
        return reviewRepository.findByBookId(bookId);
    }

    public Double avgRating(Long bookId) {
        Double avg = reviewRepository.avgRatingByBookId(bookId);
        return avg != null ? avg : 0.0;
    }

    public boolean hasReviewed(Long bookId, Long userId) {
        return reviewRepository.existsByBookIdAndUserId(bookId, userId);
    }

    // Kiểm tra user đã từng mượn và trả sách này chưa
    public boolean canReview(Long bookId, Long userId, Long readerId) {
        return borrowRecordRepository.findByReaderId(readerId).stream()
            .anyMatch(b -> b.getBook().getId().equals(bookId)
                && b.getStatus() == BorrowRecord.BorrowStatus.RETURNED);
    }

    @Transactional
    public Review addReview(Long bookId, User user, int rating, String comment) {
        // Không cho review 2 lần
        if (reviewRepository.existsByBookIdAndUserId(bookId, user.getId())) {
            throw new RuntimeException("Ban da danh gia sach nay roi");
        }
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new RuntimeException("Khong tim thay sach"));

        Review review = new Review();
        review.setBook(book);
        review.setUser(user);
        review.setRating(rating);
        review.setComment(comment);
        return reviewRepository.save(review);
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }
}