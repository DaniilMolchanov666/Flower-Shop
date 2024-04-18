package com.flowerShop.Flower_Shop.service.bot.user_state_service;

import com.flowerShop.Flower_Shop.model.Product;
import com.flowerShop.Flower_Shop.model.ShopUser;
import com.flowerShop.Flower_Shop.model.UserState;
import com.flowerShop.Flower_Shop.repository.ProductsRepository;
import com.flowerShop.Flower_Shop.repository.UserStateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayDeque;
import java.util.Optional;

@Slf4j
@Service
@Component
@RequiredArgsConstructor
public class UserStateService {

    private final UserStateRepository userStateRepository;

    private final ProductsRepository productsRepository;

    @Transactional
    public void save(UserState userState) {
        userStateRepository.save(userState);
    }

    @Transactional
    public void setState(long id, Optional<Product> product, String state) {
        var userState = UserState.builder()
                .chatId(id)
                .botState(state)
                .build();
        product.ifPresent(value -> userState.setLastViewedProduct(String.valueOf(value.getId())));
        if (userStateRepository.findAllByChatId(id).contains(userState)) {
            userStateRepository.save(userState);
        }
        userStateRepository.save(userState);
    }

    public ArrayDeque<UserState> findAllByChatId(long id) {
        return userStateRepository.findAllByChatId(id);
    }

    @Transactional(readOnly = true)
    public Optional<Product> getLasViewedProductOfUser(long chatId) {
        var userState = userStateRepository.findAllByChatId(chatId).getLast();
        if (userState.getLastViewedProduct() != null
                && productsRepository.findById(Integer.valueOf(userState.getLastViewedProduct())).isPresent()) {
            return Optional.of(productsRepository.findById(Integer.valueOf(userState.getLastViewedProduct())).get());
        }
        return Optional.empty();
    }
}
