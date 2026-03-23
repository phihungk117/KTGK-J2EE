package com.example.ktrspring.controller;

import com.example.ktrspring.entity.Enrollment;
import com.example.ktrspring.entity.Role;
import com.example.ktrspring.entity.Student;
import com.example.ktrspring.repository.EnrollmentRepository;
import com.example.ktrspring.repository.RoleRepository;
import com.example.ktrspring.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @GetMapping
    public String listUsers(Model model) {
        List<Student> listUsers = studentRepository.findAll();
        // Không thao tác với tài khoản admin gốc (để bảo vệ chống tự khoá)
        listUsers.removeIf(u -> u.getUsername().equals("admin"));
        
        model.addAttribute("listUsers", listUsers);
        return "admin/users";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng ID: " + id));
        
        // Tránh xóa tài khoản chính
        if ("admin".equals(student.getUsername())) {
             redirectAttributes.addFlashAttribute("error", "Không thể xóa tài khoản Quản trị gốc!");
             return "redirect:/admin/users";
        }

        // Xóa các đăng ký học phần trước để tránh lỗi Foreign Key
        List<Enrollment> enrolls = enrollmentRepository.findByStudent(student);
        enrollmentRepository.deleteAll(enrolls);

        // Xóa tài khoản
        studentRepository.delete(student);
        redirectAttributes.addFlashAttribute("success", "Đã xóa thành công tài khoản: " + student.getUsername());
        
        return "redirect:/admin/users";
    }

    @PostMapping("/role/{id}")
    public String toggleRole(@PathVariable("id") Long id, @RequestParam("roleName") String roleName, RedirectAttributes redirectAttributes) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng ID: " + id));

        if ("admin".equals(student.getUsername())) {
            redirectAttributes.addFlashAttribute("error", "Không thể đổi quyền tài khoản Quản trị gốc!");
            return "redirect:/admin/users";
        }

        Role targetRole = roleRepository.findByName(roleName).orElse(null);
        if (targetRole != null) {
            boolean hasRole = student.getRoles().stream().anyMatch(r -> r.getName().equals(roleName));
            if (hasRole) {
                // Nếu đã có quyền -> Tước quyền (Trừ khi đây là quyền duy nhất)
                if (student.getRoles().size() > 1) {
                    student.getRoles().removeIf(r -> r.getName().equals(roleName));
                    redirectAttributes.addFlashAttribute("success", "Đã thu hồi quyền " + roleName + " của " + student.getUsername());
                } else {
                    redirectAttributes.addFlashAttribute("error", "Tài khoản phải có ít nhất 1 quyền!");
                }
            } else {
                // Nếu chưa có quyền -> Cấp quyền
                student.getRoles().add(targetRole);
                redirectAttributes.addFlashAttribute("success", "Đã cấp quyền " + roleName + " cho " + student.getUsername());
            }
            studentRepository.save(student);
        }

        return "redirect:/admin/users";
    }
}
