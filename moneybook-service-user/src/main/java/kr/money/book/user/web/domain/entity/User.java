package kr.money.book.user.web.domain.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.List;
import kr.money.book.common.constants.Role;
import kr.money.book.rds.converters.BooleanToYNConverter;
import kr.money.book.rds.entity.MutableBaseEntity;
import kr.money.book.rds.generators.UUIDGenerator;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Singular;
import org.springframework.validation.annotation.Validated;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    name = "mb_user",
    uniqueConstraints = {
        @UniqueConstraint(name = "idx_mb_user_unique", columnNames = {"provider", "unique_key"}),
        @UniqueConstraint(name = "idx_mb_user_unique_key", columnNames = {"user_key"})
    },
    indexes = {
        @Index(name = "idx_mb_user_provider_email", columnList = "provider, email"),
        @Index(name = "idx_mb_user_update_date", columnList = "update_date")
    }
)
@Validated
public class User extends MutableBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private Long idx;

    @Column(name = "user_key", length = 100, updatable = false, unique = true, nullable = false)
    private String userKey;

    @Column(name = "name", length = 30)
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "profile", columnDefinition = "TEXT")
    private String profile;

    @Column(name = "provider", nullable = false)
    private String provider;

    @Column(name = "unique_key", nullable = false)
    private String uniqueKey;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "is_blocked", nullable = false, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Convert(converter = BooleanToYNConverter.class)
    private Boolean isBlocked = false;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<UserToken> tokens = new ArrayList<>();

    @PrePersist
    private void prePersist() {

        if (this.userKey == null) {
            this.userKey = UUIDGenerator.generate();
            if (this.getRegisterUser() == null) {
                this.setRegisterUser(this.userKey);
            }
            if (this.getUpdateUser() == null) {
                this.setUpdateUser(this.userKey);
            }
        }
    }

    @Builder
    public User(String userKey, String name, String email, String password, String profile, String provider,
        String uniqueKey, Role role, Boolean isBlocked, @Singular("userTokens") List<UserToken> tokens) {

        this.userKey = userKey;
        this.name = name;
        this.email = email;
        this.password = password;
        this.profile = profile;
        this.provider = provider;
        this.uniqueKey = uniqueKey;
        this.role = role != null ? role : Role.USER;
        this.isBlocked = isBlocked != null ? isBlocked : false;
        this.tokens = tokens;
    }

    public void updateName(String newName) {

        this.name = newName;
    }

    public void updatePassword(String newPassword) {

        this.password = newPassword;
    }

    public void block() {

        this.isBlocked = true;
    }

    public void unblock() {

        this.isBlocked = false;
    }
}
