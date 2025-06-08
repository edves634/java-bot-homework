package org.homework.api;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;

public interface CommandService {
    SendMessage startCommand(String chatId);
    SendMessage handleUserResponse(String chatId, String text);
    SendMessage showTravelOptions(String chatId, AbsSender bot);
    SendMessage sendGoodbyeMessage(String chatId);
    SendMessage getHelp(String chatId);
}
