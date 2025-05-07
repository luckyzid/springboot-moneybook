package kr.money.book.redis.constants;

public final class CommandKey {

    public static final String COMMAND_SYNC = "user:sync";
    public static final String COMMAND_DELETE = "user:delete";

    // 인스턴스 생성방지
    private CommandKey() {
    }
}
