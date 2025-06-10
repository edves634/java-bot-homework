package org.homework.logger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

/**
 * Тестовый класс для проверки функциональности LoggerImpl.
 * Использует Mockito для создания моков и проверки взаимодействий.
 */
@ExtendWith(MockitoExtension.class) // Активирует поддержку Mockito в JUnit 5
class LoggerImplTest {

    // Мок-объект для org.slf4j.Logger, который будет использоваться LoggerImpl
    @Mock
    private org.slf4j.Logger slf4jLogger;

    // Тестируемый объект, в который будет автоматически внедрен мок slf4jLogger
    @InjectMocks
    private LoggerImpl loggerImpl;

    /**
     * Метод, выполняемый перед каждым тестом.
     * В данном случае не содержит кода, так как вся настройка выполняется через аннотации.
     */
    @BeforeEach
    void setUp() {
        // Инициализация не требуется благодаря аннотациям Mockito
    }

    /**
     * Тест проверяет, что вызов debug() у LoggerImpl делегируется SLF4J логгеру.
     */
    @Test
    void debug_ShouldCallSlf4jLogger() {
        // Вызываем тестируемый метод
        loggerImpl.debug("test message");

        // Проверяем, что мок, получил вызов с правильным параметром
        verify(slf4jLogger).debug("test message");
    }

    /**
     * Тест проверяет, что вызов info() у LoggerImpl делегируется SLF4J логгеру.
     */
    @Test
    void info_ShouldCallSlf4jLogger() {
        // Вызываем тестируемый метод
        loggerImpl.info("test info");

        // Проверяем, что мок, получил вызов с правильным параметром
        verify(slf4jLogger).info("test info");
    }

    /**
     * Тест проверяет, что вызов warn() у LoggerImpl делегируется SLF4J логгеру.
     */
    @Test
    void warn_ShouldCallSlf4jLogger() {
        // Вызываем тестируемый метод
        loggerImpl.warn("test warn");

        // Проверяем, что мок, получил вызов с правильным параметром
        verify(slf4jLogger).warn("test warn");
    }

    /**
     * Тест проверяет, что вызов error() у LoggerImpl делегируется SLF4J логгеру.
     */
    @Test
    void error_ShouldCallSlf4jLogger() {
        // Вызываем тестируемый метод
        loggerImpl.error("test error");

        // Проверяем, что мок, получил вызов с правильным параметром
        verify(slf4jLogger).error("test error");
    }
}

