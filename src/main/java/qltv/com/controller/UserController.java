package qltv.com.controller;
import qltv.com.entity.BorrowRecord;
import qltv.com.entity.User;
import qltv.com.repository.BorrowRecordRepository;
import qltv.com.repository.CategoryRepository;
import qltv.com.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {
    private final BookService bookService;
    private final UserService userService;
    private final ReaderService readerService;
    private final CategoryRepository categoryRepository;
    private final BorrowRecordRepository borrowRecordRepository;

    public UserController(BookService bookService, UserService userService,
                          ReaderService readerService, CategoryRepository categoryRepository,
                          BorrowRecordRepository borrowRecordRepository) {
        this.bookService = bookService;
        this.userService = userService;
        this.readerService = readerService;
        this.categoryRepository = categoryRepository;
        this.borrowRecordRepository = borrowRecordRepository;
    }

    @GetMapping("/profile")
    public String profile(Authentication auth, Model model) {
        User user = userService.findByUsername(auth.getName()).orElseThrow();
        model.addAttribute("user", user);
        readerService.findByUserId(user.getId()).ifPresent(reader -> {
            model.addAttribute("reader", reader);
            List<BorrowRecord> allBorrows = borrowRecordRepository.findByReaderId(reader.getId());
            long dangMuon = allBorrows.stream()
                .filter(b -> b.getStatus() == BorrowRecord.BorrowStatus.BORROWED)
                .count();
            long choThanhToan = allBorrows.stream()
                .filter(b -> b.getStatus() == BorrowRecord.BorrowStatus.CONFIRMED)
                .count();
            long daTraCount = allBorrows.stream()
                .filter(b -> b.getStatus() == BorrowRecord.BorrowStatus.RETURNED)
                .count();
            long tongMuon = allBorrows.size();
            model.addAttribute("dangMuon", dangMuon);
            model.addAttribute("choThanhToan", choThanhToan);
            model.addAttribute("daTraCount", daTraCount);
            model.addAttribute("tongMuon", tongMuon);
        });
        return "user/profile";
    }
}