package com.edugroup.optimizer.compatibility.controller;

import com.edugroup.optimizer.compatibility.dto.CompatibilityScoreRequest;
import com.edugroup.optimizer.compatibility.dto.CompatibilityScoreResponse;
import com.edugroup.optimizer.compatibility.dto.CompatibilityScoreUpdateRequest;
import com.edugroup.optimizer.compatibility.service.CompatibilityScoreService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/compatibility-scores")
public class CompatibilityScoreController {

    private final CompatibilityScoreService service;

    public CompatibilityScoreController(CompatibilityScoreService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CompatibilityScoreResponse> create(@Valid @RequestBody CompatibilityScoreRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompatibilityScoreResponse> findOne(@PathVariable Long id) {
        return ResponseEntity.ok(service.findOne(id));
    }

    @GetMapping
    public ResponseEntity<Page<CompatibilityScoreResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(service.findAll(PageRequest.of(page, size)));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<CompatibilityScoreResponse>> findByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(service.findByStudent(studentId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompatibilityScoreResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody CompatibilityScoreUpdateRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
