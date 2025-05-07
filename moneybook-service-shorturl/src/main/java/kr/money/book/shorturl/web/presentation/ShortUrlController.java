package kr.money.book.shorturl.web.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.money.book.shorturl.web.application.ShortUrlService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RestController
@Tag(name = "ShortUrl", description = "짧은URL 이동")
public class ShortUrlController {

    private final ShortUrlService shortUrlService;

    public ShortUrlController(ShortUrlService shortUrlService) {
        this.shortUrlService = shortUrlService;
    }

    @GetMapping("/{shortKey}")
    @Operation(summary = "사용자 페이지 이동", description = "페이지 이동")
    public ModelAndView redirectShortUrl(@PathVariable String shortKey) {

        final ModelAndView modelAndView = new ModelAndView();
        final String redirectionUrl = shortUrlService.findShortUrl(shortKey).originalUrl();

        if (StringUtils.isEmpty(redirectionUrl)) {
            modelAndView.setViewName("404");
        } else {
            modelAndView.addObject("redirectionUrl", redirectionUrl);
            modelAndView.setViewName("shortUrl");
        }

        return modelAndView;
    }
}
