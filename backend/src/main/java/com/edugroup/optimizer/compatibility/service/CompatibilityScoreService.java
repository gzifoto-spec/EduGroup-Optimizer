package com.edugroup.optimizer.compatibility.service;

import com.edugroup.optimizer.compatibility.dto.CompatibilityScoreRequest;
import com.edugroup.optimizer.compatibility.dto.CompatibilityScoreResponse;
import com.edugroup.optimizer.compatibility.dto.CompatibilityScoreUpdateRequest;
import com.edugroup.optimizer.compatibility.entity.CompatibilityScore;
import com.edugroup.optimizer.compatibility.repository.CompatibilityScoreRepository;
import com.edugroup.optimizer.student.entity.Student;
import com.edugroup.optimizer.student.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CompatibilityScoreService {

    private final CompatibilityScoreRepository repository;
    private final StudentRepository studentRepository;

    public CompatibilityScoreService(CompatibilityScoreRepository repository, StudentRepository studentRepository) {
        this.repository = repository;
        this.studentRepository = studentRepository;
    }

    public CompatibilityScoreResponse create(CompatibilityScoreRequest request) {
        if (request.studentAId().equals(request.studentBId())) {
            throw new IllegalArgumentException("A student cannot have a compatibility score with themselves");
        }

        Long aId = Math.min(request.studentAId(), request.studentBId());
        Long bId = Math.max(request.studentAId(), request.studentBId());

        if (repository.findByStudentPair(aId, bId).isPresent()) {
            throw new IllegalStateException("A compatibility score already exists for this student pair");
        }

        Student studentA = findStudent(aId);
        Student studentB = findStudent(bId);

        CompatibilityScore entity = new CompatibilityScore();
        entity.setStudentA(studentA);
        entity.setStudentB(studentB);
        entity.setScore(request.score());
        entity.setNotes(request.notes());

        return toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public CompatibilityScoreResponse findOne(Long id) {
        return toResponse(findById(id));
    }

    @Transactional(readOnly = true)
    public Page<CompatibilityScoreResponse> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<CompatibilityScoreResponse> findByStudent(Long studentId) {
        return repository.findAllByStudentId(studentId).stream()
                .map(this::toResponse)
                .toList();
    }

    public CompatibilityScoreResponse update(Long id, CompatibilityScoreUpdateRequest request) {
        CompatibilityScore entity = findById(id);
        entity.setScore(request.score());
        entity.setNotes(request.notes());
        return toResponse(repository.save(entity));
    }

    public void delete(Long id) {
        findById(id);
        repository.deleteById(id);
    }

    private CompatibilityScore findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Compatibility score not found: " + id));
    }

    private Student findStudent(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Student not found: " + id));
    }

    private CompatibilityScoreResponse toResponse(CompatibilityScore entity) {
        return new CompatibilityScoreResponse(
                entity.getId(),
                entity.getStudentA().getId(),
                entity.getStudentA().getName(),
                entity.getStudentB().getId(),
                entity.getStudentB().getName(),
                entity.getScore(),
                entity.getNotes(),
                entity.getUpdatedAt()
        );
    }
}
