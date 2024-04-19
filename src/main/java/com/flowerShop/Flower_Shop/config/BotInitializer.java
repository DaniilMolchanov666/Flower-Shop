package com.flowerShop.Flower_Shop.config;

import com.flowerShop.Flower_Shop.FlowerShopBot;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
@Slf4j
public class BotInitializer {

    private final Logger logger = LoggerFactory.getLogger(BotInitializer.class);

    @Bean
    public TelegramBotsApi telegramBotsApi(FlowerShopBot exchangeRatesBot) throws TelegramApiException {
        var api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(exchangeRatesBot);
        logger.info("Бот успешно зарегистрирован!");
        return api;
    }
}
