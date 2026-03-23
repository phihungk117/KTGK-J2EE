package com.example.ktrspring.config;

import com.example.ktrspring.entity.Category;
import com.example.ktrspring.entity.Course;
import com.example.ktrspring.entity.Role;
import com.example.ktrspring.entity.Student;
import com.example.ktrspring.repository.CategoryRepository;
import com.example.ktrspring.repository.CourseRepository;
import com.example.ktrspring.repository.RoleRepository;
import com.example.ktrspring.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.stream.IntStream;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner initData(CourseRepository courseRepository, 
                                      CategoryRepository categoryRepository,
                                      RoleRepository roleRepository,
                                      StudentRepository studentRepository,
                                      PasswordEncoder passwordEncoder) {
        return args -> {
            Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseGet(() -> {
                Role newRole = new Role(null, "ROLE_ADMIN", null);
                return roleRepository.save(newRole);
            });
            Role studentRole = roleRepository.findByName("ROLE_STUDENT").orElseGet(() -> {
                Role newRole = new Role(null, "ROLE_STUDENT", null);
                return roleRepository.save(newRole);
            });

            Student admin = studentRepository.findByUsername("admin").orElse(null);
            if (admin == null) {
                admin = new Student();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("123456"));
                admin.setEmail("admin@example.com");
            }
            if (!admin.getRoles().contains(adminRole)) {
                admin.getRoles().add(adminRole);
                studentRepository.save(admin);
                System.out.println("====== Admin account/role initialized (admin/123456) ======");
            }

            if (categoryRepository.count() == 0) {
                Category cnnt = new Category(null, "Công nghệ thông tin", null);
                cnnt = categoryRepository.save(cnnt);
                
                Category ktdn = new Category(null, "Kinh tế doanh nghiệp", null);
                ktdn = categoryRepository.save(ktdn);

                if (courseRepository.count() == 0) {
                    Category finalCnnt = cnnt;
                    Category finalKtdn = ktdn;
                    IntStream.rangeClosed(1, 12).forEach(i -> {
                        Course course = new Course();
                        course.setName("Học phần " + i);
                        course.setCredits(i % 3 + 2); // 2, 3 or 4 credits
                        course.setLecturer("Giảng viên " + (i % 5 + 1));
                        course.setImage("https://via.placeholder.com/300x200.png/09f/fff?text=Course+" + i);
                        course.setCategory(i % 2 == 0 ? finalCnnt : finalKtdn);
                        courseRepository.save(course);
                    });
                    System.out.println("====== Dữ liệu mẫu Course đã được tạo! ======");
                }
            }
        };
    }
}
