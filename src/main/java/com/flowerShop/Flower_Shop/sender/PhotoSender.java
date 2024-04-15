package com.flowerShop.Flower_Shop.sender;

import com.flowerShop.Flower_Shop.dto.productDTO.ProductShowDTO;
import com.flowerShop.Flower_Shop.model.Product;
import com.flowerShop.Flower_Shop.repository.ProductsRepository;
import com.flowerShop.Flower_Shop.util.bot.MarkupCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.io.File;

//TODO сделать наследование, внедрить репозиторий продуктов
public class PhotoSender {

    private final static String PATH_FLOWERS = "src/main/resources/flowers/";

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

    //TODO настроить шрифты и вывод порядкового номера продукта, категории и их количества в остатке
    public static String getCaptionOfProduct(Product product) {
        return "_" + product.getName() + "\n" + product.getPrice() + " р." + "_";
    }
}
