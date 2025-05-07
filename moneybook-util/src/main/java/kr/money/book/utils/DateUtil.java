package kr.money.book.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class DateUtil {

    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Getter
    @RequiredArgsConstructor
    public enum TIMEZONE {
        UTC("UTC"),
        KST("Asia/Seoul");

        private final String code;
    }

    public static String changeTimeByZone(String dateStr, TIMEZONE originTimeZone, TIMEZONE targetTimeZone) {
        LocalDateTime ldt = LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern(DEFAULT_DATETIME_FORMAT));
        ZoneId originZone = ZoneId.of(originTimeZone.getCode());
        ZonedDateTime originZoneTime = ldt.atZone(originZone);
        ZoneId targetZoneId = ZoneId.of(targetTimeZone.getCode());
        ZonedDateTime targetZoneTime = originZoneTime.withZoneSameInstant(targetZoneId);
        DateTimeFormatter format = DateTimeFormatter.ofPattern(DEFAULT_DATETIME_FORMAT);

        return format.format(targetZoneTime);

    }

    public static LocalDateTime stringToLocalTimeZone(String dateStr) {
        LocalDateTime ldt = LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern(DEFAULT_DATETIME_FORMAT));
        return ldt;
    }
}
