package kr.money.book.abtest.context;

public final class AbTestContextHolder {

    private static final ThreadLocal<AbTestAssignment> HOLDER = new ThreadLocal<>();

    private AbTestContextHolder() {
    }

    public static void setAssignment(AbTestAssignment assignment) {
        HOLDER.set(assignment);
    }

    public static AbTestAssignment getAssignment() {
        return HOLDER.get();
    }

    public static void clear() {
        HOLDER.remove();
    }
}
