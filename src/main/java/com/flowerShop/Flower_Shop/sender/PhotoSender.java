package com.flowerShop.Flower_Shop.sender;

import com.flowerShop.Flower_Shop.model.Product;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.io.File;

//TODO сделать наследование, внедрить репозиторий продуктов
public class PhotoSender {

    private final static String PATH_FLOWERS = "/media";

    public static SendPhoto sendMessage(Update update, Product product, InlineKeyboardMarkup inlineKeyboardMarkup) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        SendPhoto sendMessage = new SendPhoto();
        sendMessage.setCaption(getCaptionOfProduct(product));
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode(ParseMode.MARKDOWN);
        sendMessage.setPhoto(new InputFile(new File(PATH_FLOWERS + product.getNameOfPhoto())));
        return sendMessage;

    }

    public static DeleteMessage deleteMessage(Update update) {
        int messageId = update.getCallbackQuery().getMessage().getMessageId();
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(messageId);

        return deleteMessage;
    }

    public static String getCaptionOfProduct(Product product) {
        return "*" + product.getName() + "*"  + "\n" + "_" + product.getPrice() + " р." + "_";
    }
}
