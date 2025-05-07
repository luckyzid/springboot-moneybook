package kr.money.book.mongodb.entity;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class MutableBaseEntity extends BaseEntity {

    @LastModifiedBy
    @Field(name = "update_user")
    private String updateUser;

    @LastModifiedDate
    @Field(name = "update_date")
    private LocalDateTime updateDate;
}
