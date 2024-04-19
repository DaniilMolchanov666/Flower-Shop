package com.flowerShop.Flower_Shop.util.bot;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class FlowerShopBotCommands {

    public static final Map.Entry<String, String> START = Map.entry("/start",
            "позволяет ознакомиться со всеми доступными на данный момент заказами");

}
