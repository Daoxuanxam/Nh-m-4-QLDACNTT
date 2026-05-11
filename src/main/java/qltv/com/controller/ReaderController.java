package qltv.com.controller;

import qltv.com.entity.BorrowRecord;
import qltv.com.repository.BorrowRecordRepository;
import qltv.com.service.ReaderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/readers")
public class ReaderController {
    private final ReaderService readerService;
    private final BorrowRecordRepository borrowRecordRepository;

    public ReaderController(ReaderService readerService,
                            BorrowRecordRepository borrowRecordRepository) {
        this.readerService = readerService;
        this.borrowRecordRepository = borrowRecordRepository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("readers", readerService.findAll());

        Map<Long, Long> borrowingCount = borrowRecordRepository.findAll().stream()
            .filter(b -> b.getStatus() == BorrowRecord.BorrowStatus.BORROWED)
            .collect(Collectors.groupingBy(
                b -> b.getReader().getId(), Collectors.counting()));
        model.addAttribute("borrowingCount", borrowingCount);
        return "admin/readers";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            readerService.delete(id);
            ra.addFlashAttribute("success", "Xóa độc giả thành công!");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/readers";
    }
}