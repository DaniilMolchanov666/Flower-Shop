package com.flowerShop.Flower_Shop.sender;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public class MultiContentMessageSender {

    public static SendMessage sendMessage(Update update, String content, InlineKeyboardMarkup markup) {
        int messageId;
        long chatId;
        if (update.hasCallbackQuery()) {
            messageId = update.getCallbackQuery().getMessage().getMessageId();
            chatId = update.getCallbackQuery().getMessage().getChatId();
        } else {
            messageId = update.getMessage().getMessageId();
            chatId = update.getMessage().getChatId();
        }
        SendMessage sendMessage1 = new SendMessage();
        sendMessage1.setText(content);
        sendMessage1.setChatId(chatId);
        sendMessage1.setReplyMarkup(markup);
        return sendMessage1;
    }

    public static DeleteMessage deleteMessage(Update update) {
        int messageId;
        long chatId;
        if (update.hasCallbackQuery()) {
            messageId = update.getCallbackQuery().getMessage().getMessageId();
            chatId = update.getCallbackQuery().getMessage().getChatId();
        } else {
            messageId = update.getMessage().getMessageId();
            chatId = update.getMessage().getChatId();
        }

        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(messageId);

        return deleteMessage;
    }
}
