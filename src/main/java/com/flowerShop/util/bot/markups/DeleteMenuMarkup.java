package com.flowerShop.util.bot.markups;

import com.flowerShop.model.Product;
import com.vdurmont.emoji.EmojiParser;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Optional;

@Getter
@Setter
public class DeleteMenuMarkup implements KeyboardMarkupCreator{

    private Optional<Product> product;

    public DeleteMenuMarkup(Optional<Product> product) {
        this.product = product;
    }

    @Override
    public InlineKeyboardMarkup createMarkup() {
        var buttonForDelete = new InlineKeyboardButton();
        buttonForDelete.setText("Удалить из корзины" + EmojiParser.parseToUnicode(":scissors:"));
        buttonForDelete.setCallbackData("DELETE_BUTTON" + this.product.map(Product::getId).orElse(null));

        var buttonForBack = new InlineKeyboardButton();
        buttonForBack.setText("Вернуться в корзину" + EmojiParser.parseToUnicode(":arrow_left:"));
        buttonForBack.setCallbackData("BACK_TO_BUCKET_BUTTON");

        return new MarkupBuilder()
                .addButton(buttonForDelete).createRow()
                .addButton(buttonForBack).createRow()
                .buildKeyboardMarkup();
    }
}
