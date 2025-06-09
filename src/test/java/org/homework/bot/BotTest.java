package org.homework.bot;

import org.homework.api.CommandService;
import org.homework.logger.Logger;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.lang.reflect.Field;

import static org.mockito.Mockito.*;

class BotTest {

    @Test
    void onUpdateReceived_StartCommand_ShouldExecuteStartCommand() throws TelegramApiException {
        CommandService commandService = mock(CommandService.class);
        Logger logger = mock(Logger.class);
        Bot bot = new Bot();

        // Инъекция через рефлексию
        setField(bot, "commandService", commandService);
        setField(bot, "logger", logger);

        when(commandService.startCommand(any())).thenReturn(new SendMessage());

        Update update = createUpdateWithText("/start");
        bot.onUpdateReceived(update);

        verify(commandService).startCommand(any());
    }

    private Update createUpdateWithText(String text) {
        Update update = new Update();
        Message message = new Message();

        Chat chat = new Chat();
        chat.setId(123L);  // Устанавливаем ID чата

        message.setChat(chat);  // Устанавливаем объект Chat в Message
        message.setText(text);
        update.setMessage(message);
        return update;
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}