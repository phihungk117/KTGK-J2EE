package com.example.ktrspring.controller;

import com.example.ktrspring.entity.Course;
import com.example.ktrspring.repository.CategoryRepository;
import com.example.ktrspring.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Controller
@RequestMapping("/admin/courses")
public class AdminCourseController {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    public String listCourses(Model model) {
        List<Course> listCourses = courseRepository.findAll();
        model.addAttribute("listCourses", listCourses);
        return "admin/courses";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("course", new Course());
        model.addAttribute("listCategories", categoryRepository.findAll());
        return "admin/course-form";
    }

    @PostMapping("/save")
    public String saveCourse(@ModelAttribute("course") Course course,
                             @RequestParam("imageFile") MultipartFile multipartFile) throws IOException {
        
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            course.setImage("/uploads/" + fileName);

            courseRepository.save(course);
            
            String uploadDir = "uploads/";
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            try (InputStream inputStream = multipartFile.getInputStream()) {
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ioe) {
                throw new IOException("Không thể lưu ảnh: " + fileName, ioe);
            }
        } else {
            // Nếu là edit mà ko chọn ảnh mới -> giữ lại ảnh cũ
            if (course.getId() != null) {
                Course existingCourse = courseRepository.findById(course.getId()).orElse(null);
                if (existingCourse != null) {
                    course.setImage(existingCourse.getImage());
                }
            }
            courseRepository.save(course);
        }

        return "redirect:/admin/courses";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid course Id:" + id));
        model.addAttribute("course", course);
        model.addAttribute("listCategories", categoryRepository.findAll());
        return "admin/course-form";
    }

    @GetMapping("/delete/{id}")
    public String deleteCourse(@PathVariable("id") Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid course Id:" + id));
        courseRepository.delete(course);
        return "redirect:/admin/courses";
    }
}
