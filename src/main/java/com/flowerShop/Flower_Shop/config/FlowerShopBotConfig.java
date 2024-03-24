package com.flowerShop.Flower_Shop.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Data
@PropertySource("application.yaml")
public class FlowerShopBotConfig {

    @Value("${bot.name}")
    String nameOfBot;

    @Value("${bot.token}")
    String tokenToAccess;
}
