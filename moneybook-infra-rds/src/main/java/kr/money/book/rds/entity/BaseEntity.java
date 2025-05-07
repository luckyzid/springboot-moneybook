package kr.money.book.rds.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public abstract class BaseEntity {

    @CreatedBy
    @Column(name = "register_user", nullable = false, updatable = false, length = 100)
    private String registerUser;

    @CreatedDate
    @Column(name = "register_date", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP()", nullable = false, insertable = false, updatable = false)
    private LocalDateTime registerDate;
}
