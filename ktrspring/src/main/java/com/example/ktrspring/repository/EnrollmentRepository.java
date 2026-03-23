package com.example.ktrspring.repository;

import com.example.ktrspring.entity.Enrollment;
import com.example.ktrspring.entity.Student;
import com.example.ktrspring.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudent(Student student);
    Optional<Enrollment> findByStudentAndCourse(Student student, Course course);
}
