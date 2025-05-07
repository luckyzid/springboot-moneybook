package kr.money.book.account.web.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import kr.money.book.common.constants.AccountType;
import kr.money.book.rds.entity.MutableBaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    name = "mb_account",
    uniqueConstraints = {
        @UniqueConstraint(name = "idx_mb_account_unique", columnNames = {"user_key", "name"})
    },
    indexes = {
        @Index(name = "idx_mb_account_user_key", columnList = "user_key"),
        @Index(name = "idx_mb_account_update_date", columnList = "update_date")
    }
)
public class Account extends MutableBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private Long idx;

    @Column(name = "user_key", nullable = false, length = 100)
    private String userKey;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private AccountType type;

    @Builder
    public Account(String userKey, String name, AccountType type) {

        this.userKey = userKey;
        this.name = name;
        this.type = type;
    }

    public void updateAccount(String newName, AccountType newType) {

        this.name = newName;
        this.type = newType;
    }
}
