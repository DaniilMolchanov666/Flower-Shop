package com.flowerShop.Flower_Shop.sender;

import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class DeleteMessageUpdate {

    public static DeleteMessage delete(Update update) {
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
