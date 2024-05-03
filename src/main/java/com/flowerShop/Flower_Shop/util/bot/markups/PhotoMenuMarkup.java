package com.flowerShop.Flower_Shop.util.bot.markups;

import com.vdurmont.emoji.EmojiParser;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public class PhotoMenuMarkup implements KeyboardMarkupCreator {
    @Override
    public InlineKeyboardMarkup createMarkup() {
        var requestButtonForward = new InlineKeyboardButton();
        requestButtonForward.setText(EmojiParser.parseToUnicode(":arrow_forward:"));
        requestButtonForward.setCallbackData("FORWARD_BUTTON");

        var requestButtonBackward = new InlineKeyboardButton();
        requestButtonBackward.setText(EmojiParser.parseToUnicode(":arrow_backward:"));
        requestButtonBackward.setCallbackData("BACKWARD_BUTTON");

        var requestButtonRequest = new InlineKeyboardButton();
        requestButtonRequest.setText("Заказать " + EmojiParser.parseToUnicode(":heart:"));
        requestButtonRequest.setCallbackData("REQUEST_BUTTON");

        var requestButtonBack = new InlineKeyboardButton();
        requestButtonBack.setText("Назад");
        requestButtonBack.setCallbackData("BACK_CATEGORIES_BUTTON");

        return new MarkupBuilder()
                .addButton(requestButtonBackward).addButton(requestButtonForward).createRow()
                .addButton(requestButtonRequest).createRow()
                .addButton(requestButtonBack).createRow()
                .buildKeyboardMarkup();
    }
}
