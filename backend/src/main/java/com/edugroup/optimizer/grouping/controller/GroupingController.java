package com.edugroup.optimizer.grouping.controller;

import com.edugroup.optimizer.grouping.dto.GenerateGroupsRequest;
import com.edugroup.optimizer.grouping.dto.GroupingSessionResponse;
import com.edugroup.optimizer.grouping.service.GroupingSessionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/groupings")
public class GroupingController {

    private final GroupingSessionService service;

    public GroupingController(GroupingSessionService service) {
        this.service = service;
    }

    @PostMapping("/generate")
    public ResponseEntity<GroupingSessionResponse> generate(@Valid @RequestBody GenerateGroupsRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.generate(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupingSessionResponse> findOne(@PathVariable Long id) {
        return ResponseEntity.ok(service.findOne(id));
    }

    @GetMapping
    public ResponseEntity<Page<GroupingSessionResponse>> findByGroupCode(
            @RequestParam String groupCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(service.findByGroupCode(groupCode, PageRequest.of(page, size)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
