package kr.money.book.shorturl.web.domain.mapper;

import kr.money.book.common.boilerplate.DomainMapper;
import kr.money.book.shorturl.web.domain.entity.ShortUrl;
import kr.money.book.shorturl.web.domain.valueobject.ShortUrlInfo;
import org.springframework.stereotype.Component;

@Component
public class ShortUrlToShortUrlInfoMapper implements DomainMapper<ShortUrl, ShortUrlInfo> {

    @Override
    public ShortUrlInfo map(ShortUrl shortUrl) {

        if (shortUrl == null) {
            return null;
        }

        return ShortUrlInfo.builder()
            .shortKey(shortUrl.getShortKey())
            .originalUrl(shortUrl.getOriginalUrl())
            .expireDate(shortUrl.getExpireDate())
            .build();
    }
}
