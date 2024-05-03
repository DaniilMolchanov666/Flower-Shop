package com.flowerShop.Flower_Shop.util.bot.markups;

import com.vdurmont.emoji.EmojiParser;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public class CategoryMenuMarkup implements KeyboardMarkupCreator{
    @Override
    public InlineKeyboardMarkup createMarkup() {

        var buttonFlowers = new InlineKeyboardButton();
        buttonFlowers.setText("Цветы\n" + EmojiParser.parseToUnicode(":tulip:"));
        buttonFlowers.setCallbackData("Цветы");

        var buttonBouquet = new InlineKeyboardButton();
        buttonBouquet.setText("Монобукет\n" + EmojiParser.parseToUnicode(":hibiscus:"));
        buttonBouquet.setCallbackData("Монобукет");

        var compositeBouquetButton = new InlineKeyboardButton();
        compositeBouquetButton.setText("Составной букет\n" + EmojiParser.parseToUnicode(":bouquet:"));
        compositeBouquetButton.setCallbackData("Составной букет");

        var bucketButton = new InlineKeyboardButton();
        bucketButton.setText("Просмотреть корзину\n" + EmojiParser.parseToUnicode(":shopping_trolley:"));
        bucketButton.setCallbackData("BUCKET_BUTTON");

        var backButton = new InlineKeyboardButton();
        backButton.setText("Назад\n" + EmojiParser.parseToUnicode(":arrow_left:"));
        backButton.setCallbackData("BACK_TO_START_BUTTON");

        return new MarkupBuilder()
                .addButton(buttonFlowers).createRow()
                .addButton(buttonBouquet).createRow()
                .addButton(compositeBouquetButton).createRow()
                .addButton(bucketButton).createRow()
                .addButton(backButton).createRow()
                .buildKeyboardMarkup();
    }
}
