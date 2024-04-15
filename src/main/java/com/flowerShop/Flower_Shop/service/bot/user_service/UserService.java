package com.flowerShop.Flower_Shop.service.bot.user_service;

import com.flowerShop.Flower_Shop.model.ShopUser;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserService {

    void save(ShopUser user);

    ShopUser findUser(Long chatId);

}
