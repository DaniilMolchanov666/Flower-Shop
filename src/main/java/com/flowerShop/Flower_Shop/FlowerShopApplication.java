package com.flowerShop.Flower_Shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@SpringBootApplication
public class FlowerShopApplication {

	public static void main(String[] args) throws TelegramApiException {
		SpringApplication.run(FlowerShopApplication.class);
	}
}
