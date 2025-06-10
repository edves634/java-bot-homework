package org.homework.logger;

import org.homework.di.annotations.Register; // Импорт аннотации для регистрации класса в DI-контейнере
import org.slf4j.Logger; // Импорт интерфейса Logger из библиотеки SLF4J
import org.slf4j.LoggerFactory; // Импорт фабрики логгеров из библиотеки SLF4J

/**
 * Реализация интерфейса ILogger, которая использует SLF4J для логирования.
 */
@Register // Аннотация для регистрации данного класса в контейнере зависимостей
public class LoggerImpl implements ILogger {
    private final Logger logger; // Логгер, используемый для записи сообщений

    /**
     * Конструктор для инъекции зависимости логгера.
     *
     * @param logger Логгер, который будет использоваться. Если передан null, создается новый логгер.
     */
    public LoggerImpl(Logger logger) {
        this.logger = logger != null ? logger : LoggerFactory.getLogger(LoggerImpl.class);
    }

    /**
     * Логирует сообщение на уровне DEBUG.
     *
     * @param message Сообщение для логирования
     */
    @Override
    public void debug(String message) {
        logger.debug(message);
    }

    /**
     * Логирует сообщение на уровне INFO.
     *
     * @param message Сообщение для логирования
     */
    @Override
    public void info(String message) {
        logger.info(message);
    }

    /**
     * Логирует сообщение на уровне WARN.
     *
     * @param message Сообщение для логирования
     */
    @Override
    public void warn(String message) {
        logger.warn(message);
    }

    /**
     * Логирует сообщение на уровне ERROR.
     *
     * @param message Сообщение для логирования
     */
    @Override
    public void error(String message) {
        logger.error(message);
    }
}


