package kr.money.book.common.constants;

public enum AccountType {
    BANK, CARD;

    public boolean equals(String type) {
        if (type == null) {
            return false;
        }
        return this.name().equalsIgnoreCase(type);
    }
}
