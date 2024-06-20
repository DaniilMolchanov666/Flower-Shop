package com.flowerShop.service.bot.user_service;

import com.flowerShop.model.ShopUser;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    void save(ShopUser user);

    ShopUser findUser(Long chatId);

}
