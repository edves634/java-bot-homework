package org.homework;

import org.homework.bot.Bot;
import org.homework.di.DIContainer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * Главный класс приложения, который запускает Telegram-бота.
 */
public class Main {
    public static void main(String[] args) {
        // Создаем контейнер для управления зависимостями
        DIContainer container = new DIContainer();
        try {
            // Создаем экземпляр TelegramBotsApi для работы с API Telegram
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

            // Регистрируем бота в TelegramBotsApi, получая его из DI-контейнера
            botsApi.registerBot(container.resolve(Bot.class));

        } catch (TelegramApiException e) {
            // Обработка исключений, возникающих при регистрации бота
            e.printStackTrace();
        }
    }
}
