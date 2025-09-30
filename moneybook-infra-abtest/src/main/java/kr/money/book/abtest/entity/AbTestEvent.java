package kr.money.book.abtest.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Entity
@Table(name = "ab_test_event")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class AbTestEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "experiment_key", nullable = false, length = 100)
    private String experimentKey;

    @Column(name = "variant", nullable = false, length = 50)
    private String variant;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "condition_name", nullable = false, length = 150)
    private String conditionName;

    @Column(name = "allocator_name", nullable = false, length = 150)
    private String allocatorName;

    @Lob
    @Column(name = "attributes_json")
    private String attributesJson;

    @CreationTimestamp
    @Column(name = "assigned_at", nullable = false, updatable = false)
    private LocalDateTime assignedAt;
}
