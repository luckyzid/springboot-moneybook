package kr.money.book.shorturl.web.domain.repository;

import java.util.Optional;
import kr.money.book.shorturl.web.domain.entity.ShortUrl;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

public interface ShortUrlRepository extends MongoRepository<ShortUrl, String> {

    Optional<ShortUrl> findByShortKey(@Param("shortKey") String shortKey);

    boolean existsByShortKey(@Param("shortKey") String shortKey);

    Optional<ShortUrl> findByOriginalUrl(@Param("originalUrl") String originalUrl);
}
