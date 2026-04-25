package com.edugroup.optimizer.grouping.dto;

import java.util.List;

public record GroupResponse(
        int groupNumber,
        List<StudentInGroupResponse> students,
        int totalCompatibility
) {
}
