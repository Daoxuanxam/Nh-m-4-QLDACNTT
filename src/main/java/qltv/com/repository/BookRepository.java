package qltv.com.repository;

import qltv.com.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByTitleContainingIgnoreCase(String title);
    List<Book> findByCategoryId(Long categoryId);
    List<Book> findByAvailableCopiesGreaterThan(int copies);

    @Query("SELECT b FROM Book b WHERE b.availableCopies > 0 ORDER BY b.id DESC")
    List<Book> findAvailableBooks();

    // 6 sách mới nhất cho trang chủ
    @Query("SELECT b FROM Book b ORDER BY b.id DESC")
    List<Book> findTop6Latest();

    // Tìm kiếm kết hợp tên + tác giả
    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Book> searchByTitleOrAuthor(@Param("keyword") String keyword);

    // Lọc theo thể loại + tìm kiếm
    @Query("SELECT b FROM Book b WHERE b.category.id = :categoryId AND (LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Book> searchByCategoryAndKeyword(@Param("categoryId") Long categoryId, @Param("keyword") String keyword);

    // Sắp xếp A-Z
    @Query("SELECT b FROM Book b ORDER BY b.title ASC")
    List<Book> findAllOrderByTitleAsc();

    // Sắp xếp Z-A
    @Query("SELECT b FROM Book b ORDER BY b.title DESC")
    List<Book> findAllOrderByTitleDesc();

    // Chỉ sách còn hàng
    @Query("SELECT b FROM Book b WHERE b.availableCopies > 0 ORDER BY b.id DESC")
    List<Book> findAllAvailable();
}