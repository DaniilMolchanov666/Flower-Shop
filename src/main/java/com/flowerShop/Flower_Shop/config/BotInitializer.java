package com.flowerShop.Flower_Shop.config;

import com.flowerShop.Flower_Shop.FlowerShopBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
@Slf4j
public class BotInitializer {

    @Bean
    public TelegramBotsApi telegramBotsApi(FlowerShopBot exchangeRatesBot) throws TelegramApiException {
        var api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(exchangeRatesBot);
        log.info("Бот успешно зарегистрирован!");
        return api;
    }
}
