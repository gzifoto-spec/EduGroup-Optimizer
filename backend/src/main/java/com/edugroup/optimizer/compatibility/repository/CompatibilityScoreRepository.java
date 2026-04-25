package com.edugroup.optimizer.compatibility.repository;

import com.edugroup.optimizer.compatibility.entity.CompatibilityScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CompatibilityScoreRepository extends JpaRepository<CompatibilityScore, Long> {

    @Query("SELECT cs FROM CompatibilityScore cs WHERE cs.studentA.id = :aId AND cs.studentB.id = :bId")
    Optional<CompatibilityScore> findByStudentPair(@Param("aId") Long aId, @Param("bId") Long bId);

    @Query("SELECT cs FROM CompatibilityScore cs WHERE cs.studentA.id = :studentId OR cs.studentB.id = :studentId")
    List<CompatibilityScore> findAllByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT cs FROM CompatibilityScore cs JOIN FETCH cs.studentA JOIN FETCH cs.studentB WHERE cs.studentA.id IN :ids AND cs.studentB.id IN :ids")
    List<CompatibilityScore> findAllByStudentIds(@Param("ids") List<Long> ids);
}
