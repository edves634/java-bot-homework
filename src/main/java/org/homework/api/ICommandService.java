package org.homework.api;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;

/**
 * Интерфейс CommandService определяет контракт для сервиса обработки команд Telegram-бота.
 * Предоставляет методы для обработки различных команд и взаимодействия с пользователем.
 */
public interface ICommandService {

    /**
     * Обрабатывает команду /start - приветственное сообщение и начальную настройку.
     *
     * @param chatId идентификатор чата с пользователем
     * @return SendMessage объект с приветственным сообщением
     */
    SendMessage startCommand(String chatId);

    /**
     * Обрабатывает ответы пользователя на предыдущие запросы бота.
     *
     * @param chatId идентификатор чата с пользователем
     * @param text текст сообщения от пользователя
     * @return SendMessage объект с ответом на пользовательский ввод
     */
    SendMessage handleUserResponse(String chatId, String text);

    /**
     * Показывает пользователю доступные варианты путешествий.
     *
     * @param chatId идентификатор чата с пользователем
     * @param bot экземпляр бота для выполнения дополнительных действий
     * @return SendMessage объект с информацией о вариантах путешествий
     */
    SendMessage showTravelOptions(String chatId, AbsSender bot);

    /**
     * Отправляет прощальное сообщение при завершении взаимодействия.
     *
     * @param chatId идентификатор чата с пользователем
     * @return SendMessage объект с прощальным сообщением
     */
    SendMessage sendGoodbyeMessage(String chatId);

    /**
     * Обрабатывает команду /help - показывает справку по доступным командам.
     *
     * @param chatId идентификатор чата с пользователем
     * @return SendMessage объект со справочной информацией
     */
    SendMessage getHelp(String chatId);
}
