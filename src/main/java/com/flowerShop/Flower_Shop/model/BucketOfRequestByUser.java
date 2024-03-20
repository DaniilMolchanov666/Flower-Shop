package com.flowerShop.Flower_Shop.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;

@Component
@AllArgsConstructor
public class BucketOfRequestByUser {

    private List<Product> listOfProductForUser;
}
