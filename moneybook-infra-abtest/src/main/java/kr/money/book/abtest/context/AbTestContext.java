package kr.money.book.abtest.context;

public final class AbTestContext {

    private AbTestContext() {
    }

    public static AbTestAssignment currentAssignment() {
        return AbTestContextHolder.getAssignment();
    }

    public static String currentVariant() {
        AbTestAssignment assignment = currentAssignment();
        return assignment == null ? null : assignment.getVariant();
    }

    public static boolean isVariant(String expected) {
        String current = currentVariant();
        return current != null && current.equalsIgnoreCase(expected);
    }
}
