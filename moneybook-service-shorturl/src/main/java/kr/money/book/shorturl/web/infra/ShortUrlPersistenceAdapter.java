package kr.money.book.shorturl.web.infra;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import kr.money.book.shorturl.web.domain.entity.ShortUrl;
import kr.money.book.shorturl.web.domain.mapper.ShortUrlInfoToShortUrlMapper;
import kr.money.book.shorturl.web.domain.mapper.ShortUrlToShortUrlInfoMapper;
import kr.money.book.shorturl.web.domain.repository.ShortUrlRepository;
import kr.money.book.shorturl.web.domain.valueobject.ShortUrlInfo;
import kr.money.book.shorturl.web.exception.ShortUrlException;
import kr.money.book.utils.StringUtil;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ShortUrlPersistenceAdapter {

    private final ShortUrlRepository shortUrlRepository;
    private final ShortUrlInfoToShortUrlMapper shortUrlInfoToShortUrlMapper;
    private final ShortUrlToShortUrlInfoMapper shortUrlToShortUrlInfoMapper;

    public ShortUrlPersistenceAdapter(
        ShortUrlRepository shortUrlRepository,
        ShortUrlInfoToShortUrlMapper shortUrlInfoToShortUrlMapper,
        ShortUrlToShortUrlInfoMapper shortUrlToShortUrlInfoMapper) {

        this.shortUrlRepository = shortUrlRepository;
        this.shortUrlInfoToShortUrlMapper = shortUrlInfoToShortUrlMapper;
        this.shortUrlToShortUrlInfoMapper = shortUrlToShortUrlInfoMapper;
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.REPEATABLE_READ,
        readOnly = false,
        rollbackFor = Exception.class
    )
    @CacheEvict(value = "shortUrlCache", key = "#shortUrlInfo.shortKey", condition = "#shortUrlInfo.shortKey != null")
    public ShortUrlInfo createShortUrl(ShortUrlInfo shortUrlInfo) {

        Optional<ShortUrl> existingShortUrl = shortUrlRepository.findByOriginalUrl(shortUrlInfo.originalUrl())
            .filter(s -> !s.isExpired());

        if (existingShortUrl.isPresent()) {
            return shortUrlToShortUrlInfoMapper.map(existingShortUrl.get());
        }

        String shortKey = shortUrlInfo.shortKey() != null ? shortUrlInfo.shortKey() : getGeneratorKey();
        if (shortKey == null) {
            throw new ShortUrlException(ShortUrlException.ErrorCode.CREATED_FAILED);
        }

        ShortUrlInfo newShortUrlInfo = ShortUrlInfo.builder()
            .shortKey(shortKey)
            .originalUrl(shortUrlInfo.originalUrl())
            .expireDate(shortUrlInfo.expireDate() != null
                ? shortUrlInfo.expireDate()
                : LocalDateTime.now().plusDays(30))
            .build();

        ShortUrl savedShortUrl = shortUrlRepository.save(shortUrlInfoToShortUrlMapper.map(newShortUrlInfo));

        return shortUrlToShortUrlInfoMapper.map(savedShortUrl);
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED,
        readOnly = true,
        rollbackFor = Exception.class
    )
    @Cacheable(value = "shortUrlCache", key = "#shortKey", unless = "#result == null")
    public ShortUrlInfo findShortUrl(String shortKey) {

        ShortUrl foundShortUrl = shortUrlRepository.findByShortKey(shortKey)
            .filter(s -> !s.isExpired())
            .orElseThrow(() -> new ShortUrlException(ShortUrlException.ErrorCode.INVALID_URL));

        return shortUrlToShortUrlInfoMapper.map(foundShortUrl);
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.REPEATABLE_READ,
        readOnly = false,
        rollbackFor = Exception.class
    )
    @CacheEvict(value = "shortUrlCache", key = "#shortKey")
    public void deleteShortUrl(String shortKey) {

        ShortUrl shortUrl = shortUrlRepository.findByShortKey(shortKey)
            .orElseThrow(() -> new ShortUrlException(ShortUrlException.ErrorCode.INVALID_URL));

        shortUrlRepository.delete(shortUrl);
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED,
        readOnly = true,
        rollbackFor = Exception.class
    )
    public List<ShortUrlInfo> getAllShortUrls() {

        return shortUrlRepository.findAll().stream()
            .map(shortUrlToShortUrlInfoMapper::map)
            .collect(Collectors.toList());
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.REPEATABLE_READ,
        readOnly = false,
        rollbackFor = Exception.class
    )
    @CacheEvict(value = "shortUrlCache", key = "#shortUrlInfo.shortKey")
    public ShortUrlInfo updateShortUrl(ShortUrlInfo shortUrlInfo) {

        ShortUrl foundShortUrl = shortUrlRepository.findByShortKey(shortUrlInfo.shortKey())
            .orElseThrow(() -> new ShortUrlException(ShortUrlException.ErrorCode.INVALID_URL));

        foundShortUrl.updateUrlAndExpireDate(shortUrlInfo.originalUrl(), shortUrlInfo.expireDate());
        ShortUrl updatedShortUrl = shortUrlRepository.save(foundShortUrl);

        return shortUrlToShortUrlInfoMapper.map(updatedShortUrl);
    }

    private String getGeneratorKey() {
        int maxLoop = 10000;
        int keySizeUpLimit = 100;

        int limitLength = 6;
        int repeat = 0;
        do {
            final String key = StringUtil.generateRandomString(limitLength);
            if (!shortUrlRepository.existsByShortKey(key)) {
                return key;
            }

            repeat++;
            if (repeat % keySizeUpLimit == 0) {
                limitLength++;
            }

        } while (repeat <= maxLoop);

        return null;
    }
}
