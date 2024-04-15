package com.flowerShop.Flower_Shop.sender;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class TextMessageSender {

    public static SendMessage sendInfo(long id, String content) {
        SendMessage sendMessage1 = new SendMessage();
        sendMessage1.setChatId(id);
        sendMessage1.setText(content);
        return sendMessage1;
    }
}
