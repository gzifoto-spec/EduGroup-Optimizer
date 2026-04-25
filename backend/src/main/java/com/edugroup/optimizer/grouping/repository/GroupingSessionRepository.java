package com.edugroup.optimizer.grouping.repository;

import com.edugroup.optimizer.grouping.entity.GroupingSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupingSessionRepository extends JpaRepository<GroupingSession, Long> {

    Page<GroupingSession> findByGroupCode(String groupCode, Pageable pageable);
}
