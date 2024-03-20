package com.flowerShop.Flower_Shop.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.management.ConstructorParameters;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class User {

    private String name;

    private String numberOfPhone;

    private long chatId;

    private Update update;

    private List<Product> listOfRequests;

    public User(long chatId, Update update) {
        this.chatId = chatId;
        this.update = update;
    }
}
