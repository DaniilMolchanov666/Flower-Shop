package com.flowerShop.Flower_Shop.sender;

import com.flowerShop.Flower_Shop.model.Product;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.io.File;

public class PhotoSender {

    private final static String PATH_FLOWERS = "./flowers";

    public static SendPhoto sendMessage(Update update, Product product, InlineKeyboardMarkup inlineKeyboardMarkup) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        String caption = "*" + product.getName() + "*" + "\n" + "_" + product.getPrice() + " р." + "_";

        SendPhoto sendMessage = new SendPhoto();
        sendMessage.setCaption(caption);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode(ParseMode.MARKDOWN);
        sendMessage.setPhoto(new InputFile(new File(PATH_FLOWERS + product.getNameOfPhoto())));
        return sendMessage;

    }
}
