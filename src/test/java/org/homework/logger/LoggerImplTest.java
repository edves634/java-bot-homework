package org.homework.logger;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.lang.reflect.Field;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class LoggerImplTest {

    @Test
    void debug_ShouldCallSlf4jLogger() {
        // Создаем мок SLF4J Logger
        org.slf4j.Logger slf4jLogger = mock(org.slf4j.Logger.class);

        // Создаем экземпляр LoggerImpl с подменой логгера через рефлексию
        LoggerImpl loggerImpl = new LoggerImpl();
        setPrivateField(loggerImpl, "logger", slf4jLogger);

        // Вызываем метод и проверяем
        loggerImpl.debug("test message");
        verify(slf4jLogger).debug("test message");
    }

    @Test
    void info_ShouldCallSlf4jLogger() {
        org.slf4j.Logger slf4jLogger = mock(org.slf4j.Logger.class);
        LoggerImpl loggerImpl = new LoggerImpl();
        setPrivateField(loggerImpl, "logger", slf4jLogger);

        loggerImpl.info("test info");
        verify(slf4jLogger).info("test info");
    }

    @Test
    void warn_ShouldCallSlf4jLogger() {
        org.slf4j.Logger slf4jLogger = mock(org.slf4j.Logger.class);
        LoggerImpl loggerImpl = new LoggerImpl();
        setPrivateField(loggerImpl, "logger", slf4jLogger);

        loggerImpl.warn("test warn");
        verify(slf4jLogger).warn("test warn");
    }

    @Test
    void error_ShouldCallSlf4jLogger() {
        org.slf4j.Logger slf4jLogger = mock(org.slf4j.Logger.class);
        LoggerImpl loggerImpl = new LoggerImpl();
        setPrivateField(loggerImpl, "logger", slf4jLogger);

        loggerImpl.error("test error");
        verify(slf4jLogger).error("test error");
    }

    private void setPrivateField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set private field", e);
        }
    }
}