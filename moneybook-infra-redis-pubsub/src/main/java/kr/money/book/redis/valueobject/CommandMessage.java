package kr.money.book.redis.valueobject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommandMessage {

    private String userKey;
    private String timestamp;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Builder
    public CommandMessage(String userKey, String timestamp) {
        this.userKey = userKey;
        this.timestamp = timestamp;
    }

    public static CommandMessage of(String userKey) {

        return new CommandMessage(userKey, LocalDateTime.now().format(FORMATTER));
    }
}
