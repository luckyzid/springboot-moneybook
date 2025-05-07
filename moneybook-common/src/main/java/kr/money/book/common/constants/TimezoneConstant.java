package kr.money.book.common.constants;

import lombok.RequiredArgsConstructor;

public final class TimezoneConstant {

    @RequiredArgsConstructor
    public enum OffsetBase {
        UTC("UTC"),
        KST("Asia/Seoul");

        private final String offset;

        @Override
        public String toString() {
            return name() + " (" + offset + ")";
        }
    }

    // 인스턴스 생성방지
    private TimezoneConstant() {
    }
}
