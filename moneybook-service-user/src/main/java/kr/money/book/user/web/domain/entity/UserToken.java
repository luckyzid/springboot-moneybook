package kr.money.book.user.web.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import kr.money.book.rds.entity.MutableBaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    name = "mb_user_token",
    indexes = {
        @Index(name = "idx_mb_user_token_user_key", columnList = "user_key"),
        @Index(name = "idx_mb_user_token_token", columnList = "token"),
        @Index(name = "idx_mb_user_token_user_key_is_active", columnList = "user_key, is_active"),
        @Index(name = "idx_mb_user_token_update_date", columnList = "update_date")
    }
)
public class UserToken extends MutableBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private Long idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_key", referencedColumnName = "user_key", nullable = false)
    private User user;

    @Column(name = "token", nullable = false, length = 512)
    private String token;

    @Column(name = "is_active", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 1")
    private int isActive;

    @Column(name = "device_info", columnDefinition = "TEXT")
    private String deviceInfo;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "expiration_time", nullable = false)
    private LocalDateTime expirationTime;

    @Builder
    public UserToken(User user, String token, int isActive, String deviceInfo, String ipAddress,
        LocalDateTime expirationTime) {

        this.user = user;
        this.token = token;
        this.isActive = isActive;
        this.deviceInfo = deviceInfo;
        this.ipAddress = ipAddress;
        this.expirationTime = expirationTime;
    }
}
