package com.flowerShop.Flower_Shop.sender;

import com.flowerShop.Flower_Shop.model.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Component
@Data
public class PhotoForSend {

    private long id;

    private Product product;

    private InlineKeyboardMarkup inlineKeyboardMarkup;

    public SendPhoto getNewSendPhoto() {

        SendPhoto sendMessage = new SendPhoto();
        sendMessage.setCaption(product.getNameOfProduct() + "\n" + product.getPrice());
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        sendMessage.setChatId(id);
        sendMessage.setPhoto(product.getImageOfProduct());
        return sendMessage;
    }
}
