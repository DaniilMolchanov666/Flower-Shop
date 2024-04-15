package com.flowerShop.Flower_Shop.service.bot.user_service;

import com.flowerShop.Flower_Shop.model.ShopUser;
import com.flowerShop.Flower_Shop.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Component
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UsersRepository usersRepository;

    @Override
    @Transactional
    public void save(ShopUser user) {
        usersRepository.save(user);
    }

    @Override
    public ShopUser findUser(Long chatId) {
        return usersRepository.findByChatId(chatId);
    }
}
