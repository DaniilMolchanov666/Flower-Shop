package com.flowerShop.Flower_Shop.sender;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
public class WelcomSender {

    public static SendMessage sendGreetingMessage(long chatId, String nameOfUser) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText("Приветствуем тебя, " + nameOfUser + "!\n" +
                "Добро пожаловать в наш цветочный магазин 'Процветание'!\n"
                + "Здесь ты можешь ознакомиться с нашими работами и выбрать для себя что-то подходящее");
        return sendMessage;
    }

}
