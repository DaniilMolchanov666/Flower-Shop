package com.flowerShop.Flower_Shop.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Data
@PropertySource("application.yaml")
public class FlowerShopBotConfig {

    @Value("${telegram.bot.name}")
    String nameOfBot;

    @Value("${telegram.bot.token}")
    String tokenToAccess;
}
