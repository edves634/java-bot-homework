package org.homework.bot;

import org.homework.api.ICommandService;
import org.homework.logger.ILogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static org.mockito.Mockito.*;

/**
 * Тестовый класс для проверки функциональности {@link Bot}.
 * Проверяет обработку входящих сообщений и команд.
 */
@ExtendWith(MockitoExtension.class) // Активируем поддержку Mockito
class BotTest {

    @Mock
    private ICommandService commandService; // Заглушка для сервиса команд

    @Mock
    private ILogger logger; // Заглушка для логгера

    @InjectMocks
    private Bot bot; // Тестируемый класс с внедренными зависимостями

    /**
     * Тест проверяет корректную обработку команды /start.
     * @throws TelegramApiException если возникла ошибка API Telegram
     */
    @Test
    void onUpdateReceived_StartCommand_ShouldExecuteStartCommand() throws TelegramApiException {
        // Arrange - подготовка тестовых данных
        when(commandService.startCommand(any())).thenReturn(new SendMessage());

        // Act - выполнение тестируемого метода
        Update update = createUpdateWithText("/start");
        bot.onUpdateReceived(update);

        // Assert - проверка результатов
        verify(commandService).startCommand(any());
    }

    /**
     * Создает тестовый объект Update с заданным текстом сообщения.
     * @param text текст сообщения
     * @return объект Update с настроенным сообщением
     */
    private Update createUpdateWithText(String text) {
        Update update = new Update();
        Message message = new Message();
        Chat chat = new Chat();

        chat.setId(123L);
        message.setChat(chat);
        message.setText(text);
        update.setMessage(message);

        return update;
    }
}
