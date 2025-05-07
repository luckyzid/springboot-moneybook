package kr.money.book.common.constants;

import java.util.Arrays;

public enum BudgetType {
    INCOME, EXPENSE;

    public static BudgetType findType(String type) {
        return Arrays.stream(BudgetType.values())
            .filter(b -> b.name().equalsIgnoreCase(type))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("No BudgetType found for: " + type));
    }

    public boolean equals(String type) {
        if (type == null) {
            return false;
        }
        return this.name().equalsIgnoreCase(type);
    }
}
