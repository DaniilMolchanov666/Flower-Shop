package com.flowerShop.Flower_Shop.util.bot.markups;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public class BackStartButtonMarkup implements KeyboardMarkupCreator{
    @Override
    public InlineKeyboardMarkup createMarkup() {
        var backStartButton = new InlineKeyboardButton();
        backStartButton.setText("Назад");
        backStartButton.setCallbackData("BACK_START_BUTTON");

        return new MarkupBuilder()
                .addButton(backStartButton)
                .createRow()
                .buildKeyboardMarkup();
    }
}
