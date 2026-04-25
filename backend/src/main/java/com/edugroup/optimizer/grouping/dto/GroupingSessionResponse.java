package com.edugroup.optimizer.grouping.dto;

import java.time.LocalDateTime;
import java.util.List;

public record GroupingSessionResponse(
        Long id,
        String name,
        String groupCode,
        int groupSize,
        LocalDateTime createdAt,
        List<GroupResponse> groups
) {
}
