package com.flowerShop.Flower_Shop.model;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class FlowerShopBotCommands {

    public static final Map.Entry<String, String> INFO = Map.entry("/info",
            "Данный бот позволят посмотреть ассортимемнт магазина и оформить заказ\n"
            + "/start позволяет ознакомиться со всеми доступными на данный момент заказами\n"
            + "/choose позволяют оформить ваш заказ\n"
            +"/request позволят отправить ваш заказ на обработку");


    public static final Map.Entry<String, String> START = Map.entry("/start",
            "позволяет ознакомиться со всеми доступными на данный момент заказами");


}
