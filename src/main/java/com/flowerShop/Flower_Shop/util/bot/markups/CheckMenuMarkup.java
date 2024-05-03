package com.flowerShop.Flower_Shop.util.bot.markups;

import com.vdurmont.emoji.EmojiParser;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public class CheckMenuMarkup implements KeyboardMarkupCreator{
    @Override
    public InlineKeyboardMarkup createMarkup() {
        var buttonForName = new InlineKeyboardButton();
        buttonForName.setText("Поменять имя" + EmojiParser.parseToUnicode(":writing_hand:"));
        buttonForName.setCallbackData("UPDATE_NAME_BUTTON");

        var buttonForPhone = new InlineKeyboardButton();
        buttonForPhone.setText("Поменять номер телефона" + EmojiParser.parseToUnicode(":telephone:"));
        buttonForPhone.setCallbackData("UPDATE_PHONE_BUTTON");

        var buttonForRequest = new InlineKeyboardButton();
        buttonForRequest.setText("Да, все верно!" + EmojiParser.parseToUnicode(":v:"));
        buttonForRequest.setCallbackData("SEND_REQUEST_BUTTON");

        var buttonForBack = new InlineKeyboardButton();
        buttonForBack.setText("Назад" + EmojiParser.parseToUnicode(":arrow_backward:"));
        buttonForBack.setCallbackData("BACK_CATEGORIES_BUTTON");

        return new MarkupBuilder()
                .addButton(buttonForName).createRow()
                .addButton(buttonForPhone).createRow()
                .addButton(buttonForRequest).createRow()
                .addButton(buttonForBack).createRow()
                .buildKeyboardMarkup();
    }
}
