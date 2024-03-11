package com.flowerShop.Flower_Shop.config;

import com.flowerShop.Flower_Shop.services.FlowerShopBot;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
@Slf4j
public class BotInitializer {

    private final Logger logger = LoggerFactory.getLogger(BotInitializer.class);

    @Autowired
    FlowerShopBot flowerShopBot;

    @EventListener({ContextRefreshedEvent.class})
    public void init() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            telegramBotsApi.registerBot(flowerShopBot);
            logger.info("Бот успешно зарегистрирован!");
        } catch (TelegramApiException e) {
            logger.error("Ошибка с регистрацией бота!");
        }
    }
}
