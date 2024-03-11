package com.flowerShop.Flower_Shop;

import com.flowerShop.Flower_Shop.services.FlowerShopBot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class FlowerShopApplication {

	public static void main(String[] args) throws TelegramApiException {
		SpringApplication.run(FlowerShopApplication.class);
	}
}
