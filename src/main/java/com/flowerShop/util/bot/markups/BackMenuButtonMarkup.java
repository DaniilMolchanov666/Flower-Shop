package com.flowerShop.util.bot.markups;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public class BackMenuButtonMarkup implements KeyboardMarkupCreator{
    @Override
    public InlineKeyboardMarkup createMarkup() {
        var backMenuButton = new InlineKeyboardButton();
        backMenuButton.setText("Назад");
        backMenuButton.setCallbackData("BACK_MENU_BUTTON");

        return new MarkupBuilder()
                .addButton(backMenuButton)
                .createRow()
                .buildKeyboardMarkup();
    }
}
