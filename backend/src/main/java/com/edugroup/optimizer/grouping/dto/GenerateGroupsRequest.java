package com.edugroup.optimizer.grouping.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GenerateGroupsRequest(
        @NotBlank String name,
        @NotBlank String groupCode,
        @NotNull @Min(2) Integer groupSize
) {
}
