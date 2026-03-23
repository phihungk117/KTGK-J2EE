package com.example.ktrspring.controller;

import com.example.ktrspring.entity.Course;
import com.example.ktrspring.entity.Enrollment;
import com.example.ktrspring.entity.Student;
import com.example.ktrspring.repository.CourseRepository;
import com.example.ktrspring.repository.EnrollmentRepository;
import com.example.ktrspring.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/enroll")
public class EnrollmentController {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentRepository studentRepository;

    @PostMapping("/{courseId}")
    public String enrollCourse(@PathVariable("courseId") Long courseId, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Student student = studentRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sinh viên"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khóa học"));

        if (enrollmentRepository.findByStudentAndCourse(student, course).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Bạn đã đăng ký khóa học này rồi!");
            return "redirect:/courses";
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrollDate(LocalDate.now());

        enrollmentRepository.save(enrollment);
        
        redirectAttributes.addFlashAttribute("success", "Đăng ký thành công khóa học: " + course.getName());
        return "redirect:/courses";
    }

    @GetMapping("/my-courses")
    public String myCourses(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Student student = studentRepository.findByUsername(username).orElse(null);
        if (student != null) {
            List<Enrollment> enrollments = enrollmentRepository.findByStudent(student);
            model.addAttribute("enrollments", enrollments);
        }

        return "my-courses";
    }
}
