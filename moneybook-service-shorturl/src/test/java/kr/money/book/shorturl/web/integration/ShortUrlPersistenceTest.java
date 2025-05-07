package kr.money.book.shorturl.web.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.List;
import kr.money.book.helper.PersistenceTest;
import kr.money.book.shorturl.web.domain.entity.ShortUrl;
import kr.money.book.shorturl.web.domain.mapper.ShortUrlInfoToShortUrlMapper;
import kr.money.book.shorturl.web.domain.mapper.ShortUrlToShortUrlInfoMapper;
import kr.money.book.shorturl.web.domain.repository.ShortUrlRepository;
import kr.money.book.shorturl.web.domain.valueobject.ShortUrlInfo;
import kr.money.book.shorturl.web.exception.ShortUrlException;
import kr.money.book.shorturl.web.infra.ShortUrlPersistenceAdapter;
import kr.money.book.utils.StringUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@PersistenceTest
public class ShortUrlPersistenceTest {

    @Autowired
    private ShortUrlRepository shortUrlRepository;

    @Autowired
    private ShortUrlPersistenceAdapter shortUrlPersistenceAdapter;

    @Autowired
    private ShortUrlInfoToShortUrlMapper shortUrlInfoToShortUrlMapper;

    @Autowired
    private ShortUrlToShortUrlInfoMapper shortUrlToShortUrlInfoMapper;

    private String randomShortKey;
    private String randomOriginalUrl;
    private LocalDateTime expireDate;

    @BeforeEach
    void setUp() {
        randomShortKey = StringUtil.generateRandomString(6);
        randomOriginalUrl = "https://example.com/" + StringUtil.generateRandomString(10);
        expireDate = LocalDateTime.now().plusDays(30);
        shortUrlRepository.deleteAll();
    }

    @AfterEach
    void cleanUp() {
        shortUrlRepository.deleteAll();
    }

    @Test
    void 단축URL저장_성공() {
        ShortUrl shortUrl = ShortUrl.builder()
            .shortKey(randomShortKey)
            .originalUrl(randomOriginalUrl)
            .expireDate(expireDate)
            .build();

        ShortUrlInfo savedShortUrlInfo = shortUrlPersistenceAdapter.createShortUrl(
            shortUrlToShortUrlInfoMapper.map(shortUrl)
        );

        assertNotNull(savedShortUrlInfo);
        assertEquals(randomShortKey, savedShortUrlInfo.shortKey());
        assertEquals(randomOriginalUrl, savedShortUrlInfo.originalUrl());
    }

    @Test
    void 단축키로단축URL찾기_성공() {
        ShortUrl shortUrl = ShortUrl.builder()
            .shortKey(randomShortKey)
            .originalUrl(randomOriginalUrl)
            .expireDate(expireDate)
            .build();
        shortUrlRepository.save(shortUrl);

        ShortUrlInfo found1 = shortUrlPersistenceAdapter.findShortUrl(randomShortKey);
        assertEquals(randomShortKey, found1.shortKey());

        ShortUrlInfo found2 = shortUrlPersistenceAdapter.findShortUrl(randomShortKey);
        assertEquals(randomShortKey, found2.shortKey());
        assertSame(found1, found2);
    }

    @Test
    void 단축URL삭제_성공() {
        ShortUrl shortUrl = ShortUrl.builder()
            .shortKey(randomShortKey)
            .originalUrl(randomOriginalUrl)
            .expireDate(expireDate)
            .build();
        shortUrlRepository.save(shortUrl);

        shortUrlPersistenceAdapter.deleteShortUrl(randomShortKey);
        assertFalse(shortUrlRepository.findByShortKey(randomShortKey).isPresent());

        assertThrows(ShortUrlException.class, () -> shortUrlPersistenceAdapter.findShortUrl(randomShortKey));
    }

    @Test
    void 단축URL생성_중복원본URL_기존항목반환() {
        ShortUrl shortUrl = ShortUrl.builder()
            .shortKey(randomShortKey)
            .originalUrl(randomOriginalUrl)
            .expireDate(expireDate)
            .build();
        shortUrlRepository.save(shortUrl);

        ShortUrlInfo newShortUrlInfo = ShortUrlInfo.builder()
            .originalUrl(randomOriginalUrl)
            .expireDate(expireDate)
            .build();

        ShortUrlInfo result = shortUrlPersistenceAdapter.createShortUrl(newShortUrlInfo);
        assertEquals(randomShortKey, result.shortKey());
    }

    @Test
    void 모든단축URL가져오기_성공() {
        String shortKey1 = StringUtil.generateRandomString(6);
        String shortKey2 = StringUtil.generateRandomString(6);
        ShortUrl shortUrl1 = ShortUrl.builder()
            .shortKey(shortKey1)
            .originalUrl("https://example1.com/" + StringUtil.generateRandomString(10))
            .expireDate(expireDate)
            .build();
        ShortUrl shortUrl2 = ShortUrl.builder()
            .shortKey(shortKey2)
            .originalUrl("https://example2.com/" + StringUtil.generateRandomString(10))
            .expireDate(expireDate)
            .build();
        shortUrlRepository.saveAll(List.of(shortUrl1, shortUrl2));

        List<ShortUrlInfo> result = shortUrlPersistenceAdapter.getAllShortUrls();
        assertEquals(2, result.size());
    }

    @Test
    void 단축URL업데이트_성공() {
        ShortUrl shortUrl = ShortUrl.builder()
            .shortKey(randomShortKey)
            .originalUrl(randomOriginalUrl)
            .expireDate(expireDate)
            .build();
        shortUrlRepository.save(shortUrl);

        String updatedUrl = "https://newexample.com/" + StringUtil.generateRandomString(10);
        LocalDateTime newExpireDate = LocalDateTime.now().plusDays(60);
        ShortUrlInfo updated = shortUrlPersistenceAdapter.updateShortUrl(
            ShortUrlInfo.builder()
                .shortKey(randomShortKey)
                .originalUrl(updatedUrl)
                .expireDate(newExpireDate)
                .build()
        );
        assertEquals(updatedUrl, updated.originalUrl());
        assertEquals(newExpireDate, updated.expireDate());
    }
}
