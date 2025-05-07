package kr.money.book.utils;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;

//boolean success = CASUtil.updateWithCAS(
//		user::getName,           // 현재 값 공급자
//		originalName,            // 기대값
//		newName,                 // 새 값
//		name -> user.updateName(name), // 업데이트 동작
//		3                        // 최대 시도 횟수
//);
//UserInfo updated = CASUtil.updateWithCAS(
//		() -> get(cacheName, key, UserInfo.class), // 현재 값 공급자
//		updater,                                   // 변환 함수 ex) Function<UserInfo, UserInfo> updater
//		newValue -> cache.put(key, newValue),      // 업데이트 동작
//		3                                          // 최대 시도 횟수
//);
@Slf4j
public class CASUtil {

    // 인스턴스 생성 방지
    private CASUtil() {
    }

    public static <T> boolean updateWithCAS(
        Supplier<T> currentValueSupplier,
        T expectedValue,
        T newValue,
        Consumer<T> updateAction,
        int maxAttempts) {
        AtomicReference<T> current = new AtomicReference<>();
        int attempts = 0;

        while (attempts < maxAttempts) {
            T currentValue = currentValueSupplier.get();
            current.set(currentValue);

            if (current.compareAndSet(currentValue, newValue)) {
                if (currentValue == null && expectedValue == null ||
                    currentValue != null && currentValue.equals(expectedValue)) {
                    updateAction.accept(newValue);
                    log.info("CAS update succeeded after {} attempts", attempts + 1);
                    return true;
                }
            }

            attempts++;
            log.warn("CAS update retry attempt {}/{}", attempts, maxAttempts);
            try {
                Thread.sleep(100); // 충돌 시 재시도 전 대기
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Interrupted during CAS retry", e);
                break;
            }
        }

        log.error("Failed to update with CAS after {} attempts", maxAttempts);
        return false;
    }

    public static <T> T updateWithCAS(
        Supplier<T> currentValueSupplier,
        Function<T, T> updater,
        Consumer<T> updateAction,
        int maxAttempts) {
        AtomicReference<T> current = new AtomicReference<>();
        int attempts = 0;

        while (attempts < maxAttempts) {
            T currentValue = currentValueSupplier.get();
            current.set(currentValue);
            T updatedValue = updater.apply(currentValue);

            if (current.compareAndSet(currentValue, updatedValue)) {
                updateAction.accept(updatedValue);
                log.info("CAS update succeeded after {} attempts", attempts + 1);
                return updatedValue;
            }

            attempts++;
            log.warn("CAS update retry attempt {}/{}", attempts, maxAttempts);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Interrupted during CAS retry", e);
                break;
            }
        }

        log.error("Failed to update with CAS after {} attempts", maxAttempts);
        return null;
    }
}
