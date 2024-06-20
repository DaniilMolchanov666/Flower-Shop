package com.flowerShop.util.bot.markups;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class MarkupBuilder {

    private final List<List<InlineKeyboardButton>> listRows = new ArrayList<>();

    private final List<InlineKeyboardButton> inlineKeyboardButtonList = new ArrayList<>();

    public MarkupBuilder addButton(InlineKeyboardButton inlineKeyboardButton) {
        this.inlineKeyboardButtonList.add(inlineKeyboardButton);
        return this;
    }

    public MarkupBuilder createRow() {
        this.listRows.add(new ArrayList<>(inlineKeyboardButtonList));
        this.inlineKeyboardButtonList.clear();
        return this;
    }

    public InlineKeyboardMarkup buildKeyboardMarkup() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(listRows);
        return inlineKeyboardMarkup;
    }
}
