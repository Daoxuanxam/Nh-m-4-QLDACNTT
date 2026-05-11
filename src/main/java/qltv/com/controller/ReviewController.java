package qltv.com.controller;

import qltv.com.entity.User;
import qltv.com.service.ReviewService;
import qltv.com.service.ReaderService;
import qltv.com.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user/review")
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;
    private final ReaderService readerService;

    public ReviewController(ReviewService reviewService,
                            UserService userService,
                            ReaderService readerService) {
        this.reviewService = reviewService;
        this.userService = userService;
        this.readerService = readerService;
    }

    @PostMapping("/add/{bookId}")
    public String addReview(@PathVariable Long bookId,
                            @RequestParam int rating,
                            @RequestParam String comment,
                            Authentication auth,
                            RedirectAttributes ra) {
        User user = userService.findByUsername(auth.getName()).orElseThrow();

        // Kiểm tra đã mượn và trả chưa
        boolean canReview = readerService.findByUserId(user.getId())
            .map(reader -> reviewService.canReview(bookId, user.getId(), reader.getId()))
            .orElse(false);

        if (!canReview) {
            ra.addFlashAttribute("error", "Ban chi co the danh gia sau khi da muon va tra sach nay!");
            return "redirect:/books/detail/" + bookId;
        }

        try {
            reviewService.addReview(bookId, user, rating, comment);
            ra.addFlashAttribute("success", "Cam on ban da danh gia sach!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/books/detail/" + bookId;
    }

    @PostMapping("/delete/{reviewId}")
    public String deleteReview(@PathVariable Long reviewId,
                               @RequestParam Long bookId,
                               RedirectAttributes ra) {
        reviewService.deleteReview(reviewId);
        ra.addFlashAttribute("success", "Da xoa danh gia.");
        return "redirect:/books/detail/" + bookId;
    }
}