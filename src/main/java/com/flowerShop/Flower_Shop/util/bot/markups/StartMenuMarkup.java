package com.flowerShop.Flower_Shop.util.bot.markups;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public class StartMenuMarkup implements KeyboardMarkupCreator{
    @Override
    public InlineKeyboardMarkup createMarkup() {
        var categoryButton = new InlineKeyboardButton();
        categoryButton.setText("Просмотреть товары ");
        categoryButton.setCallbackData("CATEGORY_BUTTON");

        var helpButton = new InlineKeyboardButton();
        helpButton.setText("Связь с магазином");
        helpButton.setCallbackData("HELP_BUTTON");

        return new MarkupBuilder()
                .addButton(categoryButton).addButton(helpButton).createRow()
                .buildKeyboardMarkup();
    }
}
