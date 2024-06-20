package com.flowerShop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.yaml")
public class FlowerShopBotConfig {

    @Value("${telegram.bot.name}")
    private String nameOfBot;

    @Value("${telegram.bot.token}")
    private String tokenToAccess;
}
