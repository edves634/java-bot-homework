package org.homework.services;

import org.homework.api.ICommandService;
import org.homework.di.annotations.Register;
import org.homework.di.annotations.Resolve;
import org.homework.logger.ILogger;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Реализация сервиса обработки команд для Telegram бота.
 * Обрабатывает команды пользователя и предоставляет соответствующие ответы.
 * Включает функционал для работы с путешествиями и туризмом.
 */
@Register
public class CommandServiceImpl implements ICommandService {

    /**
     * Логгер для записи событий и ошибок.
     * Внедряется автоматически через DI контейнер.
     */
    @Resolve
    private ILogger logger;

    /**
     * Обработка команды /start.
     * @param chatId идентификатор чата с пользователем
     * @return сообщение с приветствием и предложением начать путешествие
     */
    @Override
    public SendMessage startCommand(String chatId) {
        logger.info("Обработка команды /start для чата: " + chatId);
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Привет! Хотите отправиться в путешествие? (Да/Нет)");
        return message;
    }

    /**
     * Обработка ответа пользователя на приветственное сообщение.
     * @param chatId идентификатор чата
     * @param text текст ответа пользователя
     * @return сообщение в зависимости от ответа пользователя
     */
    @Override
    public SendMessage handleUserResponse(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);

        if (text.equalsIgnoreCase("да")) {
            message.setText("Отлично! Вот варианты для поездки, а также вы можете подобрать жилье и страховку. " +
                    "Как бонус статья, как можно зарабатывать в поездках и путешествиях в конце списка!");
            return message;
        } else if (text.equalsIgnoreCase("нет")) {
            message.setText("Жаль! Если передумаете - просто напишите /start");
            return message;
        } else {
            message.setText("Пожалуйста, введите 'Да' по-русски, если хотите продолжить, или 'Нет' для выхода.");
            return message;
        }
    }

    /**
     * Отображение вариантов путешествий с интерактивной клавиатурой.
     * @param chatId идентификатор чата
     * @param bot экземпляр бота для отправки сообщений
     * @return сообщение с кнопками вариантов путешествий
     */
    @Override
    public SendMessage showTravelOptions(String chatId, AbsSender bot) {
        logger.info("Показ вариантов путешествий для чата: " + chatId);

        // Создаем интерактивную клавиатуру
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Добавляем кнопки с различными вариантами путешествий
        rows.add(createButtonRow("1. Экскурсии", "https://tripster.tp.st/Pr5STfjY?erid=2VtzqxAsKVL"));
        rows.add(createButtonRow("2. Туры с перелетом", "https://travelata.tp.st/N5mhcEzA?erid=2Vtzqw422R8"));
        rows.add(createButtonRow("3. Санатории", "https://sanatory.tp.st/YBl7VDw8?erid=2VtzqvmBLWG"));
        rows.add(createButtonRow("4. Круизы", "https://lavoyage.tp.st/I1mwtXxV?erid=2VtzqufYFY8"));
        rows.add(createButtonRow("5. Отели", "https://hotellook.tp.st/5nalTqC1"));
        rows.add(createButtonRow("6. Квартиры посуточно", "https://sutochno.tp.st/Atl2dRdj"));
        rows.add(createButtonRow("7. Связь в роуминге", "https://yesim.tp.st/dOy2DoeY?erid=2VtzquZdJrY"));
        rows.add(createButtonRow("8. Страховка", "https://cherehapa.tp.st/BLDnBaE5?erid=2VtzqwzQ8kJ"));
        rows.add(createButtonRow("9. Как зарабатывать на путешествиях", "https://keyslady.ru/путешествуй-с-удовольствием-и-зараба/?swcfpc=1"));

        keyboardMarkup.setKeyboard(rows);

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Выберите тип путешествия:");
        message.setReplyMarkup(keyboardMarkup);

        // Запланировать отправку прощального сообщения через 5 секунд
        scheduleGoodbyeMessage(chatId, bot);

        return message;
    }

    /**
     * Создает ряд с одной кнопкой для интерактивной клавиатуры.
     * @param text текст кнопки
     * @param url URL для перехода при нажатии
     * @return список с одной кнопкой (для совместимости с API Telegram)
     */
    private List<InlineKeyboardButton> createButtonRow(String text, String url) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setUrl(url);
        return List.of(button);
    }

    /**
     * Планирует отправку прощального сообщения через 5 секунд.
     * @param chatId идентификатор чата
     * @param bot экземпляр бота для отправки
     */
    private void scheduleGoodbyeMessage(String chatId, AbsSender bot) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(() -> {
            try {
                SendMessage goodbyeMessage = sendGoodbyeMessage(chatId);
                bot.execute(goodbyeMessage);
            } catch (TelegramApiException e) {
                logger.error("Ошибка при отправке сообщения: " + e.getMessage());
            }
        }, 5, TimeUnit.SECONDS);
        scheduler.shutdown();
    }

    /**
     * Создает прощальное сообщение с дополнительной информацией.
     * @param chatId идентификатор чата
     * @return прощальное сообщение
     */
    @Override
    public SendMessage sendGoodbyeMessage(String chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Приятного путешествия! Если хотите больше информации про путешествия, " +
                "горящие туры, календарь низких цен - заходите ко мне на сайт https://keyslady.ru/");
        return message;
    }

    /**
     * Обработка команды /help.
     * @param chatId идентификатор чата
     * @return сообщение с доступными командами
     */
    @Override
    public SendMessage getHelp(String chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Доступные команды:\n/start - начать диалог");
        return message;
    }
}
