package qltv.com.controller;
import qltv.com.entity.User;
import qltv.com.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/staff")
public class StaffController {
    private final UserService userService;

    public StaffController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("staffList", userService.findByRole(User.Role.STAFF));
        model.addAttribute("newStaff", new User());
        return "admin/staff";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute User user, RedirectAttributes ra) {
        // Kiểm tra trùng username
        if (userService.existsByUsername(user.getUsername())) {
            ra.addFlashAttribute("error", "Tên đăng nhập '" + user.getUsername() + "' đã tồn tại!");
            return "redirect:/admin/staff";
        }
        // Kiểm tra trùng email
        if (userService.existsByEmail(user.getEmail())) {
            ra.addFlashAttribute("error", "Email '" + user.getEmail() + "' đã được sử dụng!");
            return "redirect:/admin/staff";
        }
        user.setRole(User.Role.STAFF);
        userService.saveUser(user);
        ra.addFlashAttribute("success", "Thêm nhân viên thành công!");
        return "redirect:/admin/staff";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        userService.deleteUser(id);
        ra.addFlashAttribute("success", "Xóa nhân viên thành công!");
        return "redirect:/admin/staff";
    }
}