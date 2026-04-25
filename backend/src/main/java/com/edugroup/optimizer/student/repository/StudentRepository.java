package com.edugroup.optimizer.student.repository;

import com.edugroup.optimizer.student.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findByGroupCodeAndActiveTrue(String groupCode);
}
