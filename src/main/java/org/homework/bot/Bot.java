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
        return "BOT_NAME";
    }

    @Override
    public String getBotToken() {
        return "BOT_TOKEN";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        String messageText = update.getMessage().getText();
        String chatId = update.getMessage().getChatId().toString();

        try {
            switch (messageText.toLowerCase()) {
                case "/start":
                    execute(commandService.startCommand(chatId));
                    break;
                case "/help":
                    execute(commandService.getHelp(chatId));
                    break;
                case "да":
                    execute(commandService.handleUserResponse(chatId, messageText));
                    execute(commandService.showTravelOptions(chatId, this));
                    break;
                default:
                    execute(commandService.handleUserResponse(chatId, messageText));
            }
        } catch (TelegramApiException e) {
            logger.error("Ошибка при отправке сообщения: " + e.getMessage());
        }
    }
}
