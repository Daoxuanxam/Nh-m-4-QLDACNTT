package qltv.com.controller;

import qltv.com.entity.*;
import qltv.com.service.*;
import qltv.com.repository.BorrowRecordRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final BookService bookService;
    private final BorrowService borrowService;
    private final ReaderService readerService;
    private final UserService userService;
    private final BorrowRecordRepository borrowRecordRepository;

    public AdminController(BookService bookService, BorrowService borrowService,
                           ReaderService readerService, UserService userService,
                           BorrowRecordRepository borrowRecordRepository) {
        this.bookService = bookService;
        this.borrowService = borrowService;
        this.readerService = readerService;
        this.userService = userService;
        this.borrowRecordRepository = borrowRecordRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalBooks", bookService.count());
        model.addAttribute("totalReaders", readerService.count());
        model.addAttribute("pendingBorrows", borrowService.countPending());
        model.addAttribute("overdueCount", borrowService.countOverdue());
        model.addAttribute("recentBorrows", borrowRecordRepository.findAll()
            .stream().sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
            .limit(5).toList());
        model.addAttribute("totalStaff", userService.countByRole(User.Role.STAFF));
        return "admin/dashboard";
    }
}