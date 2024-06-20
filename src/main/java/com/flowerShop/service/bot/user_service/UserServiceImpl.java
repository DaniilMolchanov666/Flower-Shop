package com.flowerShop.service.bot.user_service;

import com.flowerShop.model.ShopUser;
import com.flowerShop.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
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
    @Transactional(readOnly = true)
    public ShopUser findUser(Long chatId) {
        return usersRepository.findByChatId(chatId);
    }
}
