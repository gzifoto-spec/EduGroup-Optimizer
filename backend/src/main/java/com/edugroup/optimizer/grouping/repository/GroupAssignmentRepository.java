package com.edugroup.optimizer.grouping.repository;

import com.edugroup.optimizer.grouping.entity.GroupAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupAssignmentRepository extends JpaRepository<GroupAssignment, Long> {

    @Query("SELECT ga FROM GroupAssignment ga JOIN FETCH ga.student WHERE ga.groupingSession.id = :sessionId")
    List<GroupAssignment> findByGroupingSessionId(@Param("sessionId") Long sessionId);

    @Query("""
            SELECT ga FROM GroupAssignment ga
            JOIN FETCH ga.student
            JOIN FETCH ga.groupingSession
            WHERE ga.groupingSession.groupCode = :groupCode
            """)
    List<GroupAssignment> findAllByGroupCode(@Param("groupCode") String groupCode);

    void deleteAllByGroupingSessionId(Long sessionId);
}
