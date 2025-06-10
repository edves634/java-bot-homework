package org.homework.services;

import org.homework.logger.ILogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Тестовый класс для проверки функциональности CommandServiceImpl.
 * Проверяет обработку команд Telegram бота и формирование ответных сообщений.
 */
@ExtendWith(MockitoExtension.class) // Активирует поддержку Mockito в JUnit 5
class CommandServiceImplTest {

    /**
     * Мок-объект для логгера (интерфейс ILogger)
     */
    @Mock
    private ILogger logger;

    /**
     * Мок-объект для отправки сообщений через Telegram API
     */
    @Mock
    private AbsSender absSender;

    /**
     * Мок-объект для сервиса выполнения задач по расписанию
     */
    @Mock
    private ScheduledExecutorService executorService;

    /**
     * Тестируемый сервис команд, в который будут внедрены моки
     */
    @InjectMocks
    private CommandServiceImpl commandService;

    /**
     * Тестовый идентификатор чата
     */
    private final String CHAT_ID = "12345";

    /**
     * Тест проверяет корректность работы команды /start
     * Должен возвращать приветственное сообщение
     */
    @Test
    void startCommand_ShouldReturnWelcomeMessage() {
        // Act
        SendMessage result = commandService.startCommand(CHAT_ID);

        // Assert
        assertEquals(CHAT_ID, result.getChatId(), "Неверный идентификатор чата");
        assertTrue(result.getText().contains("Привет! Хотите отправиться в путешествие? (Да/Нет)"),
                "Сообщение должно содержать приветствие");
        verify(logger).info(anyString());
    }

    /**
     * Тест проверяет обработку положительного ответа пользователя ("Да")
     * Должен возвращать сообщение с вариантами путешествий
     */
    @Test
    void handleUserResponse_WithYes_ShouldReturnPositiveResponse() {
        // Act
        SendMessage result = commandService.handleUserResponse(CHAT_ID, "Да");

        // Assert
        assertEquals(CHAT_ID, result.getChatId(), "Неверный идентификатор чата");
        assertTrue(result.getText().contains("Отлично! Вот варианты для поездки"),
                "Сообщение должно содержать информацию о вариантах путешествий");
    }

    /**
     * Тест проверяет обработку отрицательного ответа пользователя ("Нет")
     * Должен возвращать сообщение с прощанием
     */
    @Test
    void handleUserResponse_WithNo_ShouldReturnNegativeResponse() {
        // Act
        SendMessage result = commandService.handleUserResponse(CHAT_ID, "Нет");

        // Assert
        assertEquals(CHAT_ID, result.getChatId(), "Неверный идентификатор чата");
        assertTrue(result.getText().contains("Жаль! Если передумаете"),
                "Сообщение должно содержать текст для случая отказа");
    }

    /**
     * Тест проверяет обработку некорректного ответа пользователя
     * Должен запрашивать уточнение ответа
     */
    @Test
    void handleUserResponse_WithInvalidInput_ShouldAskForClarification() {
        // Act
        SendMessage result = commandService.handleUserResponse(CHAT_ID, "Может быть");

        // Assert
        assertEquals(CHAT_ID, result.getChatId(), "Неверный идентификатор чата");
        assertTrue(result.getText().contains("Пожалуйста, введите 'Да' по-русски"),
                "Сообщение должно запрашивать корректный ввод");
    }

    /**
     * Тест проверяет отображение вариантов путешествий
     * Должен возвращать сообщение с инлайн-клавиатурой
     */
    @Test
    void showTravelOptions_ShouldReturnMessageWithKeyboard() {
        // Act
        SendMessage result = commandService.showTravelOptions(CHAT_ID, absSender);

        // Assert
        assertEquals(CHAT_ID, result.getChatId(), "Неверный идентификатор чата");
        assertTrue(result.getText().contains("Выберите тип путешествия"),
                "Сообщение должно содержать текст выбора типа путешествия");

        InlineKeyboardMarkup keyboard = (InlineKeyboardMarkup) result.getReplyMarkup();
        assertNotNull(keyboard, "Клавиатура должна быть создана");

        List<List<InlineKeyboardButton>> rows = keyboard.getKeyboard();
        assertEquals(9, rows.size(), "Неверное количество вариантов путешествий");
        assertTrue(rows.get(0).get(0).getText().contains("Экскурсии"),
                "Первый вариант должен быть 'Экскурсии'");
    }

    /**
     * Тест проверяет отправку прощального сообщения
     * Должен содержать текст прощания и ссылку на сайт
     */
    @Test
    void sendGoodbyeMessage_ShouldReturnCorrectText() {
        // Act
        SendMessage result = commandService.sendGoodbyeMessage(CHAT_ID);

        // Assert
        assertEquals(CHAT_ID, result.getChatId(), "Неверный идентификатор чата");
        assertTrue(result.getText().contains("Приятного путешествия!"),
                "Сообщение должно содержать прощание");
        assertTrue(result.getText().contains("keyslady.ru"),
                "Сообщение должно содержать ссылку на сайт");
    }

    /**
     * Тест проверяет команду /help
     * Должен возвращать справочное сообщение
     */
    @Test
    void getHelp_ShouldReturnHelpMessage() {
        // Act
        SendMessage result = commandService.getHelp(CHAT_ID);

        // Assert
        assertEquals(CHAT_ID, result.getChatId(), "Неверный идентификатор чата");
        assertTrue(result.getText().contains("/start - начать диалог"),
                "Сообщение должно содержать инструкции по использованию");
    }
}
