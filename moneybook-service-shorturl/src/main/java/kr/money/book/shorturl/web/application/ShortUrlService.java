package kr.money.book.shorturl.web.application;

import java.time.LocalDateTime;
import java.util.List;
import kr.money.book.shorturl.web.domain.valueobject.ShortUrlInfo;
import kr.money.book.shorturl.web.infra.ShortUrlPersistenceAdapter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShortUrlService {

    private final ShortUrlPersistenceAdapter shortUrlPersistenceAdapter;

    public ShortUrlService(ShortUrlPersistenceAdapter shortUrlPersistenceAdapter) {
        this.shortUrlPersistenceAdapter = shortUrlPersistenceAdapter;
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.REPEATABLE_READ,
        readOnly = false,
        rollbackFor = Exception.class
    )
    public ShortUrlInfo createShortUrl(ShortUrlInfo shortUrlInfo) {

        return shortUrlPersistenceAdapter.createShortUrl(shortUrlInfo);
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.REPEATABLE_READ,
        readOnly = false,
        rollbackFor = Exception.class
    )
    public ShortUrlInfo updateShortUrl(ShortUrlInfo shortUrlInfo) {

        return shortUrlPersistenceAdapter.updateShortUrl(shortUrlInfo);
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED,
        readOnly = true,
        rollbackFor = Exception.class
    )
    public ShortUrlInfo findShortUrl(String shortKey) {
        ShortUrlInfo shortUrl = shortUrlPersistenceAdapter.findShortUrl(shortKey);
        if (shortUrl.expireDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("URL has expired");
        }
        return shortUrl;
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.REPEATABLE_READ,
        readOnly = false,
        rollbackFor = Exception.class
    )
    public void deleteShortUrl(String shortKey) {

        shortUrlPersistenceAdapter.deleteShortUrl(shortKey);
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED,
        readOnly = true,
        rollbackFor = Exception.class
    )
    public List<ShortUrlInfo> getShortUrlList() {

        return shortUrlPersistenceAdapter.getAllShortUrls();
    }
}
