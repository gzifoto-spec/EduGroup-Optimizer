package com.edugroup.optimizer.compatibility.entity;

import com.edugroup.optimizer.student.entity.Student;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "compatibility_scores",
    uniqueConstraints = @UniqueConstraint(columnNames = {"student_a_id", "student_b_id"})
)
@Getter
@Setter
@NoArgsConstructor
public class CompatibilityScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_a_id", nullable = false)
    private Student studentA;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_b_id", nullable = false)
    private Student studentB;

    @Column(nullable = false)
    private Integer score;

    @Column(length = 255)
    private String notes;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    void touchUpdatedAt() {
        updatedAt = LocalDateTime.now();
    }
}
