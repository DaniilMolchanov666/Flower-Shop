package com.flowerShop.util.bot.markups;

import com.flowerShop.model.Product;
import com.vdurmont.emoji.EmojiParser;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public class    AllRequestsMenuMarkup implements KeyboardMarkupCreator{

    private final List<Product> productList;

    public AllRequestsMenuMarkup(List<Product> productList) {
        this.productList = productList;
    }

    @Override
    public InlineKeyboardMarkup createMarkup() {
        var markup = new MarkupBuilder();
        for (Product product : productList) {
            var buttonForName = new InlineKeyboardButton();
            buttonForName.setText(product.getName() + " : " + product.getPrice() + " р.");
            buttonForName.setCallbackData(String.valueOf(product.getId()));
            markup.addButton(buttonForName).createRow();
        }

        var buttonForRequest = new InlineKeyboardButton();
        buttonForRequest.setText("Заказать" + EmojiParser.parseToUnicode(":white_check_mark:"));
        buttonForRequest.setCallbackData("END_REQUEST_BUTTON");

        var buttonForPostcard = new InlineKeyboardButton();
        buttonForPostcard.setText("Написать текст для открытки" + EmojiParser.parseToUnicode(":love_letter:"));
        buttonForPostcard.setCallbackData("LETTER_BUTTON");

        var buttonForDeleteBucket = new InlineKeyboardButton();
        buttonForDeleteBucket.setText("Очистить корзину" + EmojiParser.parseToUnicode(":wastebasket:"));
        buttonForDeleteBucket.setCallbackData("DELETE_BUTTON");

        var backButton = new InlineKeyboardButton();
        backButton.setText("Назад" + EmojiParser.parseToUnicode(":arrow_left:"));
        backButton.setCallbackData("CONTINUE_BUTTON");

        return markup.addButton(buttonForRequest).createRow()
                .addButton(buttonForPostcard).createRow()
                .addButton(buttonForDeleteBucket).createRow()
                .addButton(backButton).createRow()
                .buildKeyboardMarkup();
    }
}
