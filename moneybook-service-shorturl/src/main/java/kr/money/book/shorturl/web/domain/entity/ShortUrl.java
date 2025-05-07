package kr.money.book.shorturl.web.domain.entity;

import java.time.LocalDateTime;
import kr.money.book.mongodb.entity.MutableBaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "shorturls")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class ShortUrl extends MutableBaseEntity {

    @Id
    @EqualsAndHashCode.Include
    private ObjectId id;

    @Indexed(unique = true)
    @Field(name = "short_key")
    private String shortKey;

    @Indexed
    @Field(name = "original_url")
    private String originalUrl;

    @Field(name = "expire_date")
    private LocalDateTime expireDate;

    @Builder
    public ShortUrl(ObjectId id, String shortKey, String originalUrl, LocalDateTime expireDate) {

        this.id = id;
        this.shortKey = shortKey;
        this.originalUrl = originalUrl;
        this.expireDate = expireDate;
    }

    public void updateUrlAndExpireDate(String originalUrl, LocalDateTime expireDate) {

        this.originalUrl = originalUrl;
        this.expireDate = expireDate;
    }

    public boolean isExpired() {

        return expireDate != null && expireDate.isBefore(LocalDateTime.now());
    }
}
