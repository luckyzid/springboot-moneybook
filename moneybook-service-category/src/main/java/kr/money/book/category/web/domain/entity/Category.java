package kr.money.book.category.web.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import kr.money.book.rds.entity.MutableBaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    name = "mb_category",
    uniqueConstraints = {
        @UniqueConstraint(name = "idx_mb_category_unique", columnNames = {"user_key", "name"})
    },
    indexes = {
        @Index(name = "idx_mb_category_user_key_depth", columnList = "user_key, depth"),
        @Index(name = "idx_mb_category_parent_idx", columnList = "parent_idx"),
        @Index(name = "idx_mb_category_user_key", columnList = "user_key"),
        @Index(name = "idx_mb_category_update_date", columnList = "update_date")
    }
)
public class Category extends MutableBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private Long idx;

    @Column(name = "user_key", nullable = false, length = 100)
    private String userKey;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "parent_idx", columnDefinition = "BIGINT UNSIGNED")
    private Long parentIdx;

    @Column(name = "depth", nullable = false, columnDefinition = "INT DEFAULT 0")
    private int depth;

    @Builder
    public Category(String userKey, String name, Long parentIdx, int depth) {

        this.userKey = userKey;
        this.name = name;
        this.parentIdx = parentIdx;
        this.depth = depth;
    }

    public void update(String name, Long parentIdx, int depth) {

        this.name = name;
        this.parentIdx = parentIdx;
        this.depth = depth;
    }
}
