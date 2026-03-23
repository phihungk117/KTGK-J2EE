package com.example.ktrspring.repository;

import com.example.ktrspring.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Page<Course> findAll(Pageable pageable);
    Page<Course> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
