package qltv.com.controller;

import qltv.com.entity.*;
import qltv.com.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class BorrowController {
    private final BorrowService borrowService;
    private final UserService userService;
    private final ReaderService readerService;

    public BorrowController(BorrowService borrowService, UserService userService,
                            ReaderService readerService) {
        this.borrowService = borrowService;
        this.userService = userService;
        this.readerService = readerService;
    }

    @GetMapping("/admin/borrows")
    public String adminBorrows(Model model, @RequestParam(required = false) String status) {
        if (status != null && !status.isEmpty()) {
            model.addAttribute("borrows",
                borrowService.findAll().stream()
                    .filter(b -> b.getStatus().name().equals(status)).toList());
        } else {
            model.addAttribute("borrows", borrowService.findAll());
        }
        return "admin/borrows";
    }

    @PostMapping("/admin/borrows/approve/{id}")
    public String approve(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        User staff = userService.findByUsername(auth.getName()).orElseThrow();
        borrowService.approveBorrow(id, staff.getId());
        ra.addFlashAttribute("success", "Đã duyệt! Phiếu xác nhận đã được tạo.");
        return "redirect:/admin/borrows";
    }

    @PostMapping("/admin/borrows/confirm-payment/{id}")
    public String confirmPayment(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        User staff = userService.findByUsername(auth.getName()).orElseThrow();
        borrowService.confirmPayment(id, staff.getId());
        ra.addFlashAttribute("success", "Xác nhận nhận tiền thành công! Sách đã được cho mượn.");
        return "redirect:/admin/borrows";
    }

    @PostMapping("/admin/borrows/return/{id}")
    public String returnBook(@PathVariable Long id, RedirectAttributes ra) {
        borrowService.returnBook(id);
        ra.addFlashAttribute("success", "Xác nhận trả sách thành công!");
        return "redirect:/admin/borrows";
    }

    @PostMapping("/admin/borrows/reject/{id}")
    public String reject(@PathVariable Long id, RedirectAttributes ra) {
        borrowService.rejectBorrow(id);
        ra.addFlashAttribute("warning", "Đã từ chối phiếu mượn.");
        return "redirect:/admin/borrows";
    }

    @GetMapping("/admin/borrows/receipt/{id}")
    public String viewReceiptAdmin(@PathVariable Long id, Model model) {
        BorrowRecord record = borrowService.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu mượn"));
        model.addAttribute("borrow", record);
        return "admin/receipt";
    }

    @PostMapping("/user/borrow/{bookId}")
    public String requestBorrow(@PathVariable Long bookId, Authentication auth,
                                RedirectAttributes ra) {
        User user = userService.findByUsername(auth.getName()).orElseThrow();
        try {
            borrowService.requestBorrow(bookId, user.getId());
            ra.addFlashAttribute("success", "Đăng ký mượn sách thành công! Vui lòng chờ duyệt.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/books/detail/" + bookId;
    }

    @GetMapping("/user/my-borrows")
    public String myBorrows(Authentication auth, Model model) {
        User user = userService.findByUsername(auth.getName()).orElseThrow();
        readerService.findByUserId(user.getId()).ifPresent(reader -> {
            model.addAttribute("borrows", borrowService.findByReader(reader.getId()));
            model.addAttribute("reader", reader);
        });
        return "user/my-borrows";
    }

    @GetMapping("/user/receipt/{id}")
    public String viewReceipt(@PathVariable Long id, Model model) {
        BorrowRecord record = borrowService.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu mượn"));
        model.addAttribute("borrow", record);
        return "user/receipt";
    }

    @PostMapping("/user/renew/{id}")
    public String renewBorrow(@PathVariable Long id, Authentication auth,
                              RedirectAttributes ra) {
        User user = userService.findByUsername(auth.getName()).orElseThrow();
        try {
            borrowService.renewBorrow(id, user.getId());
            ra.addFlashAttribute("success", "Gia hạn sách thành công! Hạn trả đã được gia hạn thêm 7 ngày.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/user/my-borrows";
    }
}