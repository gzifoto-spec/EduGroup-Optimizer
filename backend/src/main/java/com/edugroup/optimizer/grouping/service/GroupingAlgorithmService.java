package com.edugroup.optimizer.grouping.service;

import com.edugroup.optimizer.compatibility.entity.CompatibilityScore;
import com.edugroup.optimizer.compatibility.repository.CompatibilityScoreRepository;
import com.edugroup.optimizer.grouping.entity.GroupAssignment;
import com.edugroup.optimizer.grouping.entity.GroupingSession;
import com.edugroup.optimizer.grouping.repository.GroupAssignmentRepository;
import com.edugroup.optimizer.grouping.repository.GroupingSessionRepository;
import com.edugroup.optimizer.student.entity.Student;
import com.edugroup.optimizer.student.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class GroupingAlgorithmService {

    // Each past co-grouping subtracts this many effective-score points (rotation penalty)
    private static final int ROTATION_PENALTY = 3;
    // Caps the swap-improvement loop to keep runtime O(n²·rounds)
    private static final int MAX_IMPROVEMENT_ROUNDS = 50;

    private final StudentRepository studentRepository;
    private final CompatibilityScoreRepository compatibilityScoreRepository;
    private final GroupingSessionRepository groupingSessionRepository;
    private final GroupAssignmentRepository groupAssignmentRepository;

    public GroupingAlgorithmService(
            StudentRepository studentRepository,
            CompatibilityScoreRepository compatibilityScoreRepository,
            GroupingSessionRepository groupingSessionRepository,
            GroupAssignmentRepository groupAssignmentRepository) {
        this.studentRepository = studentRepository;
        this.compatibilityScoreRepository = compatibilityScoreRepository;
        this.groupingSessionRepository = groupingSessionRepository;
        this.groupAssignmentRepository = groupAssignmentRepository;
    }

    public GroupingSession generateGroups(String groupCode, int groupSize, String sessionName) {
        List<Student> students = studentRepository.findByGroupCodeAndActiveTrue(groupCode);
        if (students.size() < 2) {
            throw new IllegalArgumentException("At least 2 active students are required in group: " + groupCode);
        }

        List<Long> studentIds = students.stream().map(Student::getId).toList();
        List<CompatibilityScore> scores = compatibilityScoreRepository.findAllByStudentIds(studentIds);

        Map<String, Integer> compatMap = buildCompatMap(scores);
        Set<String> forbiddenPairs = buildForbiddenPairs(scores);
        Map<String, Integer> rotationHistory = buildRotationHistory(studentIds, groupCode);

        List<List<Student>> groups = greedyAssign(students, groupSize, compatMap, forbiddenPairs, rotationHistory);
        localSearchImprove(groups, compatMap, forbiddenPairs, rotationHistory);

        return persistSession(sessionName, groupCode, groupSize, groups);
    }

    private Map<String, Integer> buildCompatMap(List<CompatibilityScore> scores) {
        Map<String, Integer> map = new HashMap<>();
        for (CompatibilityScore cs : scores) {
            map.put(pairKey(cs.getStudentA().getId(), cs.getStudentB().getId()), cs.getScore());
        }
        return map;
    }

    private Set<String> buildForbiddenPairs(List<CompatibilityScore> scores) {
        return scores.stream()
                .filter(cs -> cs.getScore() <= -4)
                .map(cs -> pairKey(cs.getStudentA().getId(), cs.getStudentB().getId()))
                .collect(Collectors.toSet());
    }

    private Map<String, Integer> buildRotationHistory(List<Long> studentIds, String groupCode) {
        List<GroupAssignment> pastAssignments = groupAssignmentRepository.findAllByGroupCode(groupCode);
        Set<Long> relevantIds = new HashSet<>(studentIds);

        // Reconstruct past sessions: sessionId -> groupNumber -> [studentIds in same group]
        Map<Long, Map<Integer, List<Long>>> sessionGroups = new HashMap<>();
        for (GroupAssignment ga : pastAssignments) {
            Long sid = ga.getStudent().getId();
            if (!relevantIds.contains(sid)) continue;
            sessionGroups
                    .computeIfAbsent(ga.getGroupingSession().getId(), k -> new HashMap<>())
                    .computeIfAbsent(ga.getGroupNumber(), k -> new ArrayList<>())
                    .add(sid);
        }

        // Count how many sessions each pair has been placed together
        Map<String, Integer> history = new HashMap<>();
        for (Map<Integer, List<Long>> groupMap : sessionGroups.values()) {
            for (List<Long> group : groupMap.values()) {
                for (int i = 0; i < group.size(); i++) {
                    for (int j = i + 1; j < group.size(); j++) {
                        history.merge(pairKey(group.get(i), group.get(j)), 1, Integer::sum);
                    }
                }
            }
        }
        return history;
    }

    /**
     * Greedy phase: processes students in most-constrained-first order (most forbidden
     * relationships first) and places each into the highest-scoring compatible slot.
     * Pre-allocates ceil(n/groupSize) groups to keep sizes balanced.
     */
    private List<List<Student>> greedyAssign(
            List<Student> students,
            int groupSize,
            Map<String, Integer> compatMap,
            Set<String> forbiddenPairs,
            Map<String, Integer> rotationHistory) {

        List<Student> ordered = new ArrayList<>(students);
        ordered.sort((a, b) -> Integer.compare(
                countForbiddenWith(b, students, forbiddenPairs),
                countForbiddenWith(a, students, forbiddenPairs)));

        int numGroups = (int) Math.ceil((double) students.size() / groupSize);
        List<List<Student>> groups = new ArrayList<>();
        for (int i = 0; i < numGroups; i++) {
            groups.add(new ArrayList<>());
        }

        for (Student student : ordered) {
            int bestGroup = -1;
            double bestScore = Double.NEGATIVE_INFINITY;

            for (int g = 0; g < groups.size(); g++) {
                List<Student> group = groups.get(g);
                if (group.size() >= groupSize) continue;
                if (hasForbiddenWith(student, group, forbiddenPairs)) continue;

                double fitScore = calcFitScore(student, group, compatMap, rotationHistory);
                if (bestGroup == -1 || fitScore > bestScore) {
                    bestScore = fitScore;
                    bestGroup = g;
                }
            }

            if (bestGroup == -1) {
                // Hard constraints block all pre-allocated groups; open an overflow group
                groups.add(new ArrayList<>(List.of(student)));
            } else {
                groups.get(bestGroup).add(student);
            }
        }

        return groups;
    }

    /**
     * Hill-climbing improvement: repeatedly swaps one student from group i with one
     * from group j whenever the swap raises total effective compatibility without
     * introducing a forbidden pair. Stops when no improving swap exists or the round
     * limit is reached.
     */
    private void localSearchImprove(
            List<List<Student>> groups,
            Map<String, Integer> compatMap,
            Set<String> forbiddenPairs,
            Map<String, Integer> rotationHistory) {

        boolean improved = true;
        int rounds = 0;

        while (improved && rounds < MAX_IMPROVEMENT_ROUNDS) {
            improved = false;
            rounds++;

            for (int i = 0; i < groups.size() - 1; i++) {
                for (int j = i + 1; j < groups.size(); j++) {
                    List<Student> gi = groups.get(i);
                    List<Student> gj = groups.get(j);

                    for (int a = 0; a < gi.size(); a++) {
                        for (int b = 0; b < gj.size(); b++) {
                            Student sa = gi.get(a);
                            Student sb = gj.get(b);

                            if (!canSwap(sa, sb, gi, gj, forbiddenPairs)) continue;

                            double gain = swapGain(sa, sb, gi, gj, compatMap, rotationHistory);
                            if (gain > 0) {
                                gi.set(a, sb);
                                gj.set(b, sa);
                                improved = true;
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean canSwap(Student a, Student b,
                            List<Student> gi, List<Student> gj,
                            Set<String> forbiddenPairs) {
        for (Student s : gj) {
            if (s == b) continue;
            if (forbiddenPairs.contains(pairKey(a.getId(), s.getId()))) return false;
        }
        for (Student s : gi) {
            if (s == a) continue;
            if (forbiddenPairs.contains(pairKey(b.getId(), s.getId()))) return false;
        }
        return true;
    }

    private double swapGain(Student a, Student b,
                            List<Student> gi, List<Student> gj,
                            Map<String, Integer> compatMap,
                            Map<String, Integer> rotationHistory) {
        double before = 0, after = 0;
        for (Student s : gi) {
            if (s == a) continue;
            before += effectiveScore(a.getId(), s.getId(), compatMap, rotationHistory);
            after += effectiveScore(b.getId(), s.getId(), compatMap, rotationHistory);
        }
        for (Student s : gj) {
            if (s == b) continue;
            before += effectiveScore(b.getId(), s.getId(), compatMap, rotationHistory);
            after += effectiveScore(a.getId(), s.getId(), compatMap, rotationHistory);
        }
        return after - before;
    }

    private double effectiveScore(Long aId, Long bId,
                                  Map<String, Integer> compatMap,
                                  Map<String, Integer> rotationHistory) {
        String key = pairKey(aId, bId);
        return compatMap.getOrDefault(key, 0) - (double) ROTATION_PENALTY * rotationHistory.getOrDefault(key, 0);
    }

    private double calcFitScore(Student student, List<Student> group,
                                Map<String, Integer> compatMap,
                                Map<String, Integer> rotationHistory) {
        return group.stream()
                .mapToDouble(s -> effectiveScore(student.getId(), s.getId(), compatMap, rotationHistory))
                .sum();
    }

    private boolean hasForbiddenWith(Student student, List<Student> group, Set<String> forbiddenPairs) {
        return group.stream()
                .anyMatch(s -> forbiddenPairs.contains(pairKey(student.getId(), s.getId())));
    }

    private int countForbiddenWith(Student student, List<Student> all, Set<String> forbiddenPairs) {
        return (int) all.stream()
                .filter(s -> !s.getId().equals(student.getId()))
                .filter(s -> forbiddenPairs.contains(pairKey(student.getId(), s.getId())))
                .count();
    }

    private String pairKey(Long aId, Long bId) {
        return Math.min(aId, bId) + "-" + Math.max(aId, bId);
    }

    private GroupingSession persistSession(String name, String groupCode, int groupSize,
                                           List<List<Student>> groups) {
        GroupingSession session = new GroupingSession();
        session.setName(name);
        session.setGroupCode(groupCode);
        session.setGroupSize(groupSize);
        GroupingSession saved = groupingSessionRepository.save(session);

        List<GroupAssignment> assignments = new ArrayList<>();
        for (int i = 0; i < groups.size(); i++) {
            int groupNum = i + 1;
            for (Student student : groups.get(i)) {
                GroupAssignment ga = new GroupAssignment();
                ga.setGroupingSession(saved);
                ga.setStudent(student);
                ga.setGroupNumber(groupNum);
                assignments.add(ga);
            }
        }
        groupAssignmentRepository.saveAll(assignments);

        return saved;
    }
}
