package com.flowerShop.sender;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public class MultiContentMessageSender {

    public static SendMessage sendMessage(Update update, String content, InlineKeyboardMarkup markup) {
        long chatId;
        if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
        } else {
            chatId = update.getMessage().getChatId();
        }
        SendMessage sendMessage1 = new SendMessage();
        sendMessage1.setText(content);
        sendMessage1.setChatId(chatId);
        sendMessage1.setReplyMarkup(markup);
        return sendMessage1;
    }
}
