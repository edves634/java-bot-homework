package org.homework.bot;

import org.homework.api.CommandService;
import org.homework.logger.Logger;
import org.homework.di.annotations.Register;
import org.homework.di.annotations.Resolve;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Register
public class Bot extends TelegramLongPollingBot {
    @Resolve
    private CommandService commandService;
    @Resolve
    private Logger logger;

    @Override
    public String getBotUsername() {
        return "Туристический консультант";
    }

    @Override
    public String getBotToken() {
        return "7567008903:AAGyaVyzUWzt4EQRdCgYOYAECs0EEbvH5Kk";
    }

    @Override
    public void onUpdateReceived(Update update) {
        logger.debug("Получено обновление: " + update.toString());

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();

            try {
                if (messageText.equalsIgnoreCase("/start")) {
                    execute(commandService.startCommand(chatId));
                } else if (messageText.equalsIgnoreCase("/help")) {
                    execute(commandService.getHelp(chatId));
                } else if (messageText.equalsIgnoreCase("да")) {
                    // Сначала отправляем текстовое сообщение
                    execute(commandService.handleUserResponse(chatId, messageText));
                    // Затем отправляем клавиатуру с вариантами
                    execute(commandService.showTravelOptions(chatId, this));
                } else {
                    execute(commandService.handleUserResponse(chatId, messageText));
                }
            } catch (TelegramApiException e) {
                logger.error("Ошибка при отправке сообщения: " + e.getMessage());
            }
        }
    }
}