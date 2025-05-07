package kr.money.book.shorturl.web.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import kr.money.book.shorturl.web.domain.valueobject.ShortUrlInfo;
import kr.money.book.shorturl.web.infra.ShortUrlPersistenceAdapter;
import kr.money.book.utils.StringUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ShortUrlServiceTest {

    @Mock
    private ShortUrlPersistenceAdapter shortUrlPersistenceAdapter;

    @InjectMocks
    private ShortUrlService shortUrlService;

    private String randomShortKey;
    private String randomOriginalUrl;
    private LocalDateTime expireDate;

    @BeforeEach
    void setUp() {
        randomShortKey = StringUtil.generateRandomString(6);
        randomOriginalUrl = "https://example.com/" + StringUtil.generateRandomString(10);
        expireDate = LocalDateTime.now().plusDays(30);
    }

    @Test
    void 단축URL생성_성공() {
        ShortUrlInfo testShortUrl = ShortUrlInfo.builder()
            .shortKey(randomShortKey)
            .originalUrl(randomOriginalUrl)
            .expireDate(expireDate)
            .build();

        when(shortUrlPersistenceAdapter.createShortUrl(any(ShortUrlInfo.class))).thenReturn(testShortUrl);

        ShortUrlInfo result = shortUrlService.createShortUrl(testShortUrl);

        assertNotNull(result);
        assertEquals(randomShortKey, result.shortKey());
        assertEquals(randomOriginalUrl, result.originalUrl());
        verify(shortUrlPersistenceAdapter).createShortUrl(any(ShortUrlInfo.class));
    }

    @Test
    void 단축URL생성_중복키실패() {
        ShortUrlInfo testShortUrl = ShortUrlInfo.builder()
            .shortKey(randomShortKey)
            .originalUrl(randomOriginalUrl)
            .expireDate(expireDate)
            .build();

        when(shortUrlPersistenceAdapter.createShortUrl(any(ShortUrlInfo.class)))
            .thenThrow(new RuntimeException("Duplicate short key"));

        assertThrows(RuntimeException.class, () -> 
            shortUrlService.createShortUrl(testShortUrl));
    }

    @Test
    void 단축URL생성_잘못된URL형식실패() {
        ShortUrlInfo testShortUrl = ShortUrlInfo.builder()
            .shortKey(randomShortKey)
            .originalUrl("invalid-url")
            .expireDate(expireDate)
            .build();

        when(shortUrlPersistenceAdapter.createShortUrl(any(ShortUrlInfo.class)))
            .thenThrow(new RuntimeException("Invalid URL format"));

        assertThrows(RuntimeException.class, () -> 
            shortUrlService.createShortUrl(testShortUrl));
    }

    @Test
    void 단축URL생성_과거만료일실패() {
        ShortUrlInfo testShortUrl = ShortUrlInfo.builder()
            .shortKey(randomShortKey)
            .originalUrl(randomOriginalUrl)
            .expireDate(LocalDateTime.now().minusDays(1))
            .build();

        when(shortUrlPersistenceAdapter.createShortUrl(any(ShortUrlInfo.class)))
            .thenThrow(new RuntimeException("Expire date cannot be in the past"));

        assertThrows(RuntimeException.class, () -> 
            shortUrlService.createShortUrl(testShortUrl));
    }

    @Test
    void 단축URL조회_성공() {
        ShortUrlInfo testShortUrl = ShortUrlInfo.builder()
            .shortKey(randomShortKey)
            .originalUrl(randomOriginalUrl)
            .expireDate(expireDate)
            .build();

        when(shortUrlPersistenceAdapter.findShortUrl(randomShortKey)).thenReturn(testShortUrl);

        ShortUrlInfo result = shortUrlService.findShortUrl(randomShortKey);

        assertNotNull(result);
        assertEquals(randomShortKey, result.shortKey());
        assertEquals(randomOriginalUrl, result.originalUrl());
        verify(shortUrlPersistenceAdapter).findShortUrl(randomShortKey);
    }

    @Test
    void 단축URL조회_존재하지않는키실패() {
        when(shortUrlPersistenceAdapter.findShortUrl(randomShortKey))
            .thenThrow(new RuntimeException("Short URL not found"));

        assertThrows(RuntimeException.class, () -> 
            shortUrlService.findShortUrl(randomShortKey));
    }

    @Test
    void 단축URL조회_만료된URL실패() {
        ShortUrlInfo expiredShortUrl = ShortUrlInfo.builder()
            .shortKey(randomShortKey)
            .originalUrl(randomOriginalUrl)
            .expireDate(LocalDateTime.now().minusDays(1))
            .build();

        when(shortUrlPersistenceAdapter.findShortUrl(randomShortKey)).thenReturn(expiredShortUrl);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            shortUrlService.findShortUrl(randomShortKey));
        
        assertEquals("URL has expired", exception.getMessage());
    }

    @Test
    void 단축URL삭제_성공() {
        doNothing().when(shortUrlPersistenceAdapter).deleteShortUrl(randomShortKey);

        shortUrlService.deleteShortUrl(randomShortKey);

        verify(shortUrlPersistenceAdapter).deleteShortUrl(randomShortKey);
    }

    @Test
    void 단축URL삭제_존재하지않는키실패() {
        doThrow(new RuntimeException("Short URL not found"))
            .when(shortUrlPersistenceAdapter).deleteShortUrl(randomShortKey);

        assertThrows(RuntimeException.class, () -> 
            shortUrlService.deleteShortUrl(randomShortKey));
    }

    @Test
    void 단축URL목록조회_성공() {
        ShortUrlInfo testShortUrl = ShortUrlInfo.builder()
            .shortKey(randomShortKey)
            .originalUrl(randomOriginalUrl)
            .expireDate(expireDate)
            .build();

        when(shortUrlPersistenceAdapter.getAllShortUrls()).thenReturn(List.of(testShortUrl));

        List<ShortUrlInfo> result = shortUrlService.getShortUrlList();

        assertEquals(1, result.size());
        assertEquals(randomShortKey, result.get(0).shortKey());
        verify(shortUrlPersistenceAdapter).getAllShortUrls();
    }

    @Test
    void 단축URL업데이트_성공() {
        String updatedUrl = "https://newexample.com/" + StringUtil.generateRandomString(10);
        LocalDateTime newExpireDate = LocalDateTime.now().plusDays(60);
        ShortUrlInfo updatedShortUrl = ShortUrlInfo.builder()
            .shortKey(randomShortKey)
            .originalUrl(updatedUrl)
            .expireDate(newExpireDate)
            .build();

        when(shortUrlPersistenceAdapter.updateShortUrl(any(ShortUrlInfo.class))).thenReturn(updatedShortUrl);

        ShortUrlInfo result = shortUrlService.updateShortUrl(updatedShortUrl);

        assertNotNull(result);
        assertEquals(updatedUrl, result.originalUrl());
        assertEquals(newExpireDate, result.expireDate());
        verify(shortUrlPersistenceAdapter).updateShortUrl(any(ShortUrlInfo.class));
    }

    @Test
    void 단축URL업데이트_존재하지않는키실패() {
        ShortUrlInfo updatedShortUrl = ShortUrlInfo.builder()
            .shortKey(randomShortKey)
            .originalUrl(randomOriginalUrl)
            .expireDate(expireDate)
            .build();

        when(shortUrlPersistenceAdapter.updateShortUrl(any(ShortUrlInfo.class)))
            .thenThrow(new RuntimeException("Short URL not found"));

        assertThrows(RuntimeException.class, () -> 
            shortUrlService.updateShortUrl(updatedShortUrl));
    }

    @Test
    void 단축URL업데이트_잘못된URL형식실패() {
        ShortUrlInfo updatedShortUrl = ShortUrlInfo.builder()
            .shortKey(randomShortKey)
            .originalUrl("invalid-url")
            .expireDate(expireDate)
            .build();

        when(shortUrlPersistenceAdapter.updateShortUrl(any(ShortUrlInfo.class)))
            .thenThrow(new RuntimeException("Invalid URL format"));

        assertThrows(RuntimeException.class, () -> 
            shortUrlService.updateShortUrl(updatedShortUrl));
    }

    @Test
    void 단축URL업데이트_과거만료일실패() {
        ShortUrlInfo updatedShortUrl = ShortUrlInfo.builder()
            .shortKey(randomShortKey)
            .originalUrl(randomOriginalUrl)
            .expireDate(LocalDateTime.now().minusDays(1))
            .build();

        when(shortUrlPersistenceAdapter.updateShortUrl(any(ShortUrlInfo.class)))
            .thenThrow(new RuntimeException("Expire date cannot be in the past"));

        assertThrows(RuntimeException.class, () -> 
            shortUrlService.updateShortUrl(updatedShortUrl));
    }
}
