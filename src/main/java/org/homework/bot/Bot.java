package org.homework.bot;

import org.homework.api.ICommandService;
import org.homework.logger.ILogger;
import org.homework.di.annotations.Register;
import org.homework.di.annotations.Resolve;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Основной класс Telegram-бота, реализующий долгополлющую (long-polling) версию бота.
 * Обрабатывает входящие сообщения и делегирует выполнение команд сервису CommandService.
 *
 * <p>Использует DI-аннотации ({@code @Register} и {@code @Resolve}) для автоматического
 * управления зависимостями.</p>
 */
@Register
public class Bot extends TelegramLongPollingBot {

    /** Сервис для обработки команд бота, внедряемый через DI */
    @Resolve
    private ICommandService commandService;

    /** Логгер для записи ошибок и информации, внедряемый через DI */
    @Resolve
    private ILogger logger;

    /**
     * Возвращает имя бота, заданное при регистрации в Telegram.
     *
     * @return имя бота (должно быть заменено на актуальное)
     */
    @Override
    public String getBotUsername() {
        return "BOT_NAME"; // TODO: Заменить на реальное имя бота
    }

    /**
     * Возвращает токен бота, полученный от BotFather.
     *
     * @return токен бота (должен быть заменен на актуальный)
     */
    @Override
    public String getBotToken() {
        return "BOT_TOKEN"; // TODO: Заменить на реальный токен
    }

    /**
     * Основной метод обработки входящих сообщений.
     * Определяет тип сообщения и вызывает соответствующие методы CommandService.
     *
     * @param update объект Update от Telegram API с данными входящего сообщения
     */
    @Override
    public void onUpdateReceived(Update update) {
        // Игнорируем сообщения без текста
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
                    // Обработка положительного ответа с показом вариантов
                    execute(commandService.handleUserResponse(chatId, messageText));
                    execute(commandService.showTravelOptions(chatId, this));
                    break;
                default:
                    // Обработка всех остальных текстовых сообщений
                    execute(commandService.handleUserResponse(chatId, messageText));
            }
        } catch (TelegramApiException e) {
            logger.error("Ошибка при отправке сообщения: " + e.getMessage());
        }
    }
}
