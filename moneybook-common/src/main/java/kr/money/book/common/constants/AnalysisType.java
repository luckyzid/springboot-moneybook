package kr.money.book.common.constants;

public enum AnalysisType {
    MONTHLY, WEEKLY;

    public boolean equals(String type) {
        if (type == null) {
            return false;
        }
        return this.name().equalsIgnoreCase(type);
    }
}
