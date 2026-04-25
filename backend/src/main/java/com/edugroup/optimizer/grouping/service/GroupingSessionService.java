package com.edugroup.optimizer.grouping.service;

import com.edugroup.optimizer.compatibility.entity.CompatibilityScore;
import com.edugroup.optimizer.compatibility.repository.CompatibilityScoreRepository;
import com.edugroup.optimizer.grouping.dto.GenerateGroupsRequest;
import com.edugroup.optimizer.grouping.dto.GroupResponse;
import com.edugroup.optimizer.grouping.dto.GroupingSessionResponse;
import com.edugroup.optimizer.grouping.dto.StudentInGroupResponse;
import com.edugroup.optimizer.grouping.entity.GroupAssignment;
import com.edugroup.optimizer.grouping.entity.GroupingSession;
import com.edugroup.optimizer.grouping.repository.GroupAssignmentRepository;
import com.edugroup.optimizer.grouping.repository.GroupingSessionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class GroupingSessionService {

    private final GroupingAlgorithmService algorithmService;
    private final GroupingSessionRepository sessionRepository;
    private final GroupAssignmentRepository assignmentRepository;
    private final CompatibilityScoreRepository compatibilityScoreRepository;

    public GroupingSessionService(
            GroupingAlgorithmService algorithmService,
            GroupingSessionRepository sessionRepository,
            GroupAssignmentRepository assignmentRepository,
            CompatibilityScoreRepository compatibilityScoreRepository) {
        this.algorithmService = algorithmService;
        this.sessionRepository = sessionRepository;
        this.assignmentRepository = assignmentRepository;
        this.compatibilityScoreRepository = compatibilityScoreRepository;
    }

    public GroupingSessionResponse generate(GenerateGroupsRequest request) {
        GroupingSession session = algorithmService.generateGroups(
                request.groupCode(), request.groupSize(), request.name());
        return buildResponse(session);
    }

    @Transactional(readOnly = true)
    public GroupingSessionResponse findOne(Long id) {
        return buildResponse(findById(id));
    }

    @Transactional(readOnly = true)
    public Page<GroupingSessionResponse> findByGroupCode(String groupCode, Pageable pageable) {
        return sessionRepository.findByGroupCode(groupCode, pageable).map(this::buildResponse);
    }

    public void delete(Long id) {
        findById(id);
        assignmentRepository.deleteAllByGroupingSessionId(id);
        sessionRepository.deleteById(id);
    }

    private GroupingSession findById(Long id) {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Grouping session not found: " + id));
    }

    private GroupingSessionResponse buildResponse(GroupingSession session) {
        List<GroupAssignment> assignments = assignmentRepository.findByGroupingSessionId(session.getId());

        List<Long> studentIds = assignments.stream()
                .map(ga -> ga.getStudent().getId())
                .distinct()
                .toList();

        Map<String, Integer> compatMap = studentIds.size() < 2
                ? Collections.emptyMap()
                : buildCompatMap(compatibilityScoreRepository.findAllByStudentIds(studentIds));

        List<GroupResponse> groups = assignments.stream()
                .collect(Collectors.groupingBy(GroupAssignment::getGroupNumber))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> toGroupResponse(e.getKey(), e.getValue(), compatMap))
                .toList();

        return new GroupingSessionResponse(
                session.getId(), session.getName(), session.getGroupCode(),
                session.getGroupSize(), session.getCreatedAt(), groups);
    }

    private GroupResponse toGroupResponse(int groupNumber, List<GroupAssignment> assignments,
                                          Map<String, Integer> compatMap) {
        List<StudentInGroupResponse> students = assignments.stream()
                .map(ga -> new StudentInGroupResponse(ga.getStudent().getId(), ga.getStudent().getName()))
                .toList();

        int totalCompatibility = 0;
        for (int i = 0; i < assignments.size(); i++) {
            for (int j = i + 1; j < assignments.size(); j++) {
                Long aId = assignments.get(i).getStudent().getId();
                Long bId = assignments.get(j).getStudent().getId();
                totalCompatibility += compatMap.getOrDefault(Math.min(aId, bId) + "-" + Math.max(aId, bId), 0);
            }
        }

        return new GroupResponse(groupNumber, students, totalCompatibility);
    }

    private Map<String, Integer> buildCompatMap(List<CompatibilityScore> scores) {
        Map<String, Integer> map = new HashMap<>();
        for (CompatibilityScore cs : scores) {
            Long aId = cs.getStudentA().getId();
            Long bId = cs.getStudentB().getId();
            map.put(Math.min(aId, bId) + "-" + Math.max(aId, bId), cs.getScore());
        }
        return map;
    }
}
