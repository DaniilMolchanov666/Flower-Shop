package com.flowerShop.Flower_Shop.sender;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class TextMessageSender {

    public static SendMessage sendInfo(long id, String content) {
        SendMessage sendMessage1 = new SendMessage();
        sendMessage1.setChatId(id);
        sendMessage1.setText(content);
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

    public static DeleteMessage deleteMessageByBot(Update update, Message message) {

        long chatId;
        if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
        } else {
            chatId = update.getMessage().getChatId();
        }

        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(message.getMessageId());

        return deleteMessage;
    }
}
