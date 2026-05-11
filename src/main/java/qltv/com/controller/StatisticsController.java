package qltv.com.controller;

import qltv.com.entity.BorrowRecord;
import qltv.com.repository.BorrowRecordRepository;
import qltv.com.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/statistics")
public class StatisticsController {

    private final BookService bookService;
    private final BorrowService borrowService;
    private final ReaderService readerService;
    private final BorrowRecordRepository borrowRecordRepository;

    public StatisticsController(BookService bookService, BorrowService borrowService,
                                ReaderService readerService,
                                BorrowRecordRepository borrowRecordRepository) {
        this.bookService = bookService;
        this.borrowService = borrowService;
        this.readerService = readerService;
        this.borrowRecordRepository = borrowRecordRepository;
    }

    @GetMapping
    public String statistics(Model model) {
        List<BorrowRecord> all = borrowRecordRepository.findAll();

        model.addAttribute("totalBooks", bookService.count());
        model.addAttribute("totalReaders", readerService.count());
        model.addAttribute("totalBorrows", all.size());
        model.addAttribute("pendingBorrows", borrowService.countPending());
        model.addAttribute("overdueCount", borrowService.countOverdue());

        Map<String, Long> byStatus = all.stream()
            .collect(Collectors.groupingBy(b -> b.getStatus().name(), Collectors.counting()));
        model.addAttribute("borrowsByStatus", byStatus);

        Map<String, Long> topBooks = all.stream()
            .collect(Collectors.groupingBy(b -> b.getBook().getTitle(), Collectors.counting()))
            .entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(5)
            .collect(Collectors.toMap(
                Map.Entry::getKey, Map.Entry::getValue,
                (e1, e2) -> e1, LinkedHashMap::new));
        model.addAttribute("topBooks", topBooks);

        return "admin/statistics";
    }
}