package framework.utils;

import io.qameta.allure.Allure;
import org.awaitility.Awaitility;
import org.awaitility.core.ConditionTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * Утильный класс для ожидания условий и значений с помощью Awaitility.
 * Используется для стабильных проверок асинхронных состояний (например, колбэков, статусов заявок и т.д.)
 */
public class WaitHelper {

    private static final Logger log = LoggerFactory.getLogger(WaitHelper.class);

    private static final long DEFAULT_TIMEOUT_SECONDS = 30;
    private static final long DEFAULT_POLL_INTERVAL_SECONDS = 3;

    private WaitHelper() {
        // утильный класс
    }

    /**
     * Ожидает, пока условие вернёт true.
     *
     * @param condition       условие для проверки
     * @param timeoutSeconds  максимальное время ожидания (секунды)
     * @param pollSeconds     интервал между проверками (секунды)
     */
    public static void waitUntil(Callable<Boolean> condition, long timeoutSeconds, long pollSeconds) {
        Allure.step(String.format("Ожидание условия (таймаут %d сек, интервал %d сек)", timeoutSeconds, pollSeconds));
        try {
            Awaitility.await()
                    .atMost(timeoutSeconds, TimeUnit.SECONDS)
                    .pollInterval(pollSeconds, TimeUnit.SECONDS)
                    .ignoreExceptions()
                    .until(() -> {
                        boolean result = condition.call();
                        log.info("Проверка условия → {}", result);
                        return result;
                    });
        } catch (ConditionTimeoutException e) {
            String message = String.format("❌ Условие не выполнилось за %d секунд", timeoutSeconds);
            log.error(message, e);
            Allure.step(message);
            throw e;
        } catch (Exception e) {
            log.error("Ошибка при ожидании условия", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Универсальный метод ожидания с дефолтными параметрами (30 сек, 3 сек).
     */
    public static void waitUntil(Callable<Boolean> condition) {
        waitUntil(condition, DEFAULT_TIMEOUT_SECONDS, DEFAULT_POLL_INTERVAL_SECONDS);
    }

    /**
     * Ожидает, пока supplier вернёт не-null значение, удовлетворяющее предикату.
     *
     * @param supplier        источник данных (например, вызов API)
     * @param validator       условие проверки значения
     * @param timeoutSeconds  максимальное время ожидания
     * @param pollSeconds     интервал между проверками
     * @param <T>             тип возвращаемого значения
     * @return значение, удовлетворяющее условию
     */
    public static <T> T waitForValue(Callable<T> supplier, Predicate<T> validator,
                                     long timeoutSeconds, long pollSeconds) {
        Allure.step(String.format("Ожидание значения (таймаут %d сек, интервал %d сек)", timeoutSeconds, pollSeconds));
        try {
            return Awaitility.await()
                    .atMost(Duration.ofSeconds(timeoutSeconds))
                    .pollInterval(Duration.ofSeconds(pollSeconds))
                    .ignoreExceptions()
                    .until(supplier, result -> {
                        boolean valid = result != null && validator.test(result);
                        log.info("Проверка значения → {}", valid);
                        return valid;
                    });
        } catch (ConditionTimeoutException e) {
            String message = String.format("❌ Значение не удовлетворяет условию за %d секунд", timeoutSeconds);
            log.error(message, e);
            Allure.step(message);
            throw e;
        } catch (Exception e) {
            log.error("Ошибка при ожидании значения", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Упрощённая версия — просто ждёт, пока supplier вернёт не-null.
     */
    public static <T> T waitForValue(Callable<T> supplier, long timeoutSeconds) {
        return waitForValue(supplier, value -> true, timeoutSeconds, DEFAULT_POLL_INTERVAL_SECONDS);
    }

    /**
     * Дефолтная версия (30 сек, 3 сек).
     */
    public static <T> T waitForValue(Callable<T> supplier) {
        return waitForValue(supplier, value -> true, DEFAULT_TIMEOUT_SECONDS, DEFAULT_POLL_INTERVAL_SECONDS);
    }
}
