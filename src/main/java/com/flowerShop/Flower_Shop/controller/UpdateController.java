package com.flowerShop.Flower_Shop.controller;

import com.flowerShop.Flower_Shop.service.FlowerShopBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

//@Component
@Slf4j
public class UpdateController {

    public void checkUpdate(Update update) {
        if (update == null) {
            log.error("Update by user equals null!");
        } else if (update.getMessage().hasText()) {

        } else if (update.hasCallbackQuery()) {

        } else if (update.hasEditedMessage()) {

        }
    }



}
