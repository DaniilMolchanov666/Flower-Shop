package com.flowerShop.Flower_Shop.sender;

import com.flowerShop.Flower_Shop.model.Flower;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Component
@Data
public class PhotoForSend {

    private long id;

    private Flower product;

    private InlineKeyboardMarkup inlineKeyboardMarkup;

    public SendPhoto getNewSendPhoto() {

        SendPhoto sendMessage = new SendPhoto();
        sendMessage.setCaption(product.getNameOfProduct() + "\n" + product.getPrice() + " р.");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        sendMessage.setChatId(id);
        sendMessage.setPhoto(product.getImageOfProduct());
        return sendMessage;
    }
}
