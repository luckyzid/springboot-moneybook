package kr.money.book.shorturl.web.domain.mapper;

import kr.money.book.common.boilerplate.DomainMapper;
import kr.money.book.shorturl.web.domain.entity.ShortUrl;
import kr.money.book.shorturl.web.domain.valueobject.ShortUrlInfo;
import org.springframework.stereotype.Component;

@Component
public class ShortUrlInfoToShortUrlMapper implements DomainMapper<ShortUrlInfo, ShortUrl> {

    @Override
    public ShortUrl map(ShortUrlInfo shortUrlInfo) {

        return ShortUrl.builder()
            .shortKey(shortUrlInfo.shortKey())
            .originalUrl(shortUrlInfo.originalUrl())
            .expireDate(shortUrlInfo.expireDate())
            .build();
    }
}
