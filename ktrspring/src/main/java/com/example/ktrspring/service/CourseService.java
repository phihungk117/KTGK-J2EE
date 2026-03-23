package com.example.ktrspring.service;

import com.example.ktrspring.entity.Course;
import com.example.ktrspring.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    public Page<Course> getCoursesWithPagination(String keyword, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        if (keyword != null && !keyword.trim().isEmpty()) {
            return courseRepository.findByNameContainingIgnoreCase(keyword.trim(), pageable);
        }
        return courseRepository.findAll(pageable);
    }
}
