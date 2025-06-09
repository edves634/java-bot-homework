package org.homework.services;

import org.homework.logger.Logger;
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

@ExtendWith(MockitoExtension.class)
class CommandServiceImplTest {

    @Mock
    private Logger logger;

    @Mock
    private AbsSender absSender;

    @Mock
    private ScheduledExecutorService executorService;

    @InjectMocks
    private CommandServiceImpl commandService;

    private final String CHAT_ID = "12345";

    @Test
    void startCommand_ShouldReturnWelcomeMessage() {
        SendMessage result = commandService.startCommand(CHAT_ID);

        assertEquals(CHAT_ID, result.getChatId());
        assertTrue(result.getText().contains("Привет! Хотите отправиться в путешествие? (Да/Нет)"));
        verify(logger).info(anyString());
    }

    @Test
    void handleUserResponse_WithYes_ShouldReturnPositiveResponse() {
        SendMessage result = commandService.handleUserResponse(CHAT_ID, "Да");

        assertEquals(CHAT_ID, result.getChatId());
        assertTrue(result.getText().contains("Отлично! Вот варианты для поездки, а также вы можете подобрать жилье и страховку. Как бонус статья, как можно зарабатывать в поездках и путешествиях в конце списка!"));
    }

    @Test
    void handleUserResponse_WithNo_ShouldReturnNegativeResponse() {
        SendMessage result = commandService.handleUserResponse(CHAT_ID, "Нет");

        assertEquals(CHAT_ID, result.getChatId());
        assertTrue(result.getText().contains("Жаль! Если передумаете - просто напишите /start"));
    }

    @Test
    void handleUserResponse_WithInvalidInput_ShouldAskForClarification() {
        SendMessage result = commandService.handleUserResponse(CHAT_ID, "Может быть");

        assertEquals(CHAT_ID, result.getChatId());
        assertTrue(result.getText().contains("Пожалуйста, введите 'Да' по-русски, если хотите продолжить, или 'Нет' для выхода."));
    }

    @Test
    void showTravelOptions_ShouldReturnMessageWithKeyboard() {
        SendMessage result = commandService.showTravelOptions(CHAT_ID, absSender);

        assertEquals(CHAT_ID, result.getChatId());
        assertTrue(result.getText().contains("Выберите тип путешествия"));

        InlineKeyboardMarkup keyboard = (InlineKeyboardMarkup) result.getReplyMarkup();
        assertNotNull(keyboard);

        List<List<InlineKeyboardButton>> rows = keyboard.getKeyboard();
        assertEquals(9, rows.size()); // Проверяем количество вариантов
        assertTrue(rows.get(0).get(0).getText().contains("Экскурсии"));
    }

    @Test
    void sendGoodbyeMessage_ShouldReturnCorrectText() {
        SendMessage result = commandService.sendGoodbyeMessage(CHAT_ID);

        assertEquals(CHAT_ID, result.getChatId());
        assertTrue(result.getText().contains("Приятного путешествия!"));
        assertTrue(result.getText().contains("keyslady.ru"));
    }

    @Test
    void getHelp_ShouldReturnHelpMessage() {
        SendMessage result = commandService.getHelp(CHAT_ID);

        assertEquals(CHAT_ID, result.getChatId());
        assertTrue(result.getText().contains("/start - начать диалог"));
    }
}