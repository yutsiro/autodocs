package ru.nsu.astakhov.autodocs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nsu.astakhov.autodocs.model.Course;
import ru.nsu.astakhov.autodocs.model.Specialization;
import ru.nsu.astakhov.autodocs.model.StudentEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<StudentEntity, Long> {
    Optional<StudentEntity> findByFullName(String fullName);
    List<StudentEntity> findByCourseAndSpecialization(Course course, Specialization specialization);
    List<StudentEntity> findByCourse(Course course);
}
