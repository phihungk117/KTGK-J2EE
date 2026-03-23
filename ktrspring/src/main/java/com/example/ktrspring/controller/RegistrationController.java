package com.example.ktrspring.controller;

import com.example.ktrspring.entity.Role;
import com.example.ktrspring.entity.Student;
import com.example.ktrspring.repository.RoleRepository;
import com.example.ktrspring.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;

@Controller
public class RegistrationController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("student", new Student());
        return "register";
    }

    @PostMapping("/register")
    public String registerStudent(@Valid @ModelAttribute("student") Student student, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        
        if (studentRepository.existsByUsername(student.getUsername())) {
            bindingResult.rejectValue("username", "error.student", "Tên đăng nhập này đã có người sử dụng!");
            return "register";
        }
        if (studentRepository.existsByEmail(student.getEmail())) {
            bindingResult.rejectValue("email", "error.student", "Email này đã được đăng ký trong hệ thống!");
            return "register";
        }

        student.setPassword(passwordEncoder.encode(student.getPassword()));

        Role defaultRole = roleRepository.findByName("ROLE_STUDENT").orElseGet(() -> {
            Role newRole = new Role();
            newRole.setName("ROLE_STUDENT");
            return roleRepository.save(newRole);
        });

        student.getRoles().add(defaultRole);
        studentRepository.save(student);

        return "redirect:/login?registered=true";
    }
    
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }
}
