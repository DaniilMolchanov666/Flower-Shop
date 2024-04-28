package com.flowerShop.Flower_Shop.util.bot;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class FlowerShopBotCommands {

    public static final Map.Entry<String, String> START = Map.entry("/start",
            "начать работу бота или перезагрузить его");

}
