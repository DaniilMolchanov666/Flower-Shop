package com.flowerShop.Flower_Shop.sender;


import com.flowerShop.Flower_Shop.util.bot.MarkupCreator;
import org.telegram.telegrambots.meta.api.interfaces.Validable;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public class MultiContentMessageSender {

    public static SendMessage sendMessage(long idOfUpdate, String content, InlineKeyboardMarkup markup) {

        SendMessage sendMessage1 = new SendMessage();
        sendMessage1.setText(content);
        sendMessage1.setChatId(idOfUpdate);
        sendMessage1.setReplyMarkup(markup);
        return sendMessage1;
    }

    public static DeleteMessage deleteMessage(Update update) {
        int messageId = update.getCallbackQuery().getMessage().getMessageId();
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(messageId);

        return deleteMessage;
    }
}
