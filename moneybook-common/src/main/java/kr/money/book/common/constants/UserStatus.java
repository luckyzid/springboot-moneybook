package kr.money.book.common.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserStatus {
    ACTIVATE(1),
    DEACTIVATE(0);

    private final int status;
}
