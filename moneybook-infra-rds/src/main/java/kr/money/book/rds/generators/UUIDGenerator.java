package kr.money.book.rds.generators;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class UUIDGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final AtomicLong LAST_TIME = new AtomicLong();
    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public static String generate() {

        return UUID.randomUUID().toString();
    }

    public String generateBase64() {

        UUID uuid = UUID.randomUUID();
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());

        return Base64.getUrlEncoder().withoutPadding().encodeToString(byteBuffer.array()).toUpperCase(Locale.ROOT);
    }

    public static String generateId() {

        long now = System.currentTimeMillis();
        long last = LAST_TIME.getAndSet(now);

        if (now == last) {
            now = LAST_TIME.incrementAndGet();
        }
        int randomPart = RANDOM.nextInt(999999);

        return encodeBase62(now) + encodeBase62(randomPart);
    }

    private static String encodeBase62(long value) {

        StringBuilder sb = new StringBuilder();
        while (value > 0) {
            sb.append(BASE62.charAt((int) (value % 62)));
            value /= 62;
        }

        return sb.reverse().toString();
    }
}
