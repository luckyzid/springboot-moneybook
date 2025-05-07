package kr.money.book.user.configure.interceptors.context;

public class LocalContextHolder {

    private static final ThreadLocal<String> INTERNAL_CACHE_THREAD_LOCAL = new ThreadLocal<>();

    public static void setContext(String value) {
        INTERNAL_CACHE_THREAD_LOCAL.set(value);
    }

    public static String getContext() {
        return INTERNAL_CACHE_THREAD_LOCAL.get();
    }

    public static void clearContext() {
        INTERNAL_CACHE_THREAD_LOCAL.remove();
    }
}
