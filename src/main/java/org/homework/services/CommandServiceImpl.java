package org.homework.services;

import org.homework.api.CommandService;
import org.homework.di.annotations.Register;
import org.homework.di.annotations.Resolve;
import org.homework.logger.Logger;
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

@Register
public class CommandServiceImpl implements CommandService {
    @Resolve
    private Logger logger;

    @Override
    public SendMessage startCommand(String chatId) {
        logger.info("Обработка команды /start для чата: " + chatId);
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Привет! Хотите отправиться в путешествие? (Да/Нет)");
        return message;
    }

    @Override
    public SendMessage handleUserResponse(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);

        if (text.equalsIgnoreCase("да")) {
            message.setText("Отлично! Сейчас покажем варианты.");
            return message;
        } else if (text.equalsIgnoreCase("нет")) {
            message.setText("Жаль! Если передумаете - просто напишите /start");
            return message;
        } else {
            message.setText("Пожалуйста, введите 'Да' по-русски, если хотите продолжить, или 'Нет' для выхода.");
            return message;
        }
    }

    @Override
    public SendMessage showTravelOptions(String chatId, AbsSender bot) {
        logger.info("Показ вариантов путешествий для чата: " + chatId);

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        rows.add(createButtonRow("1. Экскурсии", "https://tripster.tp.st/Pr5STfjY?erid=2VtzqxAsKVL"));
        rows.add(createButtonRow("2. Туры с перелетом", "https://travelata.tp.st/N5mhcEzA?erid=2Vtzqw422R8"));
        rows.add(createButtonRow("3. Санатории", "https://sanatory.tp.st/YBl7VDw8?erid=2VtzqvmBLWG"));
        rows.add(createButtonRow("4. Круизы", "https://lavoyage.tp.st/I1mwtXxV?erid=2VtzqufYFY8"));

        keyboardMarkup.setKeyboard(rows);

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Выберите тип путешествия:");
        message.setReplyMarkup(keyboardMarkup);

        scheduleGoodbyeMessage(chatId, bot);

        return message;
    }

    private List<InlineKeyboardButton> createButtonRow(String text, String url) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setUrl(url);
        return List.of(button);
    }

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

    @Override
    public SendMessage sendGoodbyeMessage(String chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Приятного путешествия! Если хотите больше информации про путешествия, горящие туры, календарь низких цен - заходите ко мне на сайт https://keyslady.ru/");
        return message;
    }

    @Override
    public SendMessage getHelp(String chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Доступные команды:\n/start - начать диалог");
        return message;
    }
}
