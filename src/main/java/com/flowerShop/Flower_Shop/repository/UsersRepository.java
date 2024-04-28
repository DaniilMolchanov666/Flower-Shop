package com.flowerShop.Flower_Shop.repository;

import com.flowerShop.Flower_Shop.model.ShopUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<ShopUser, Long> {

    ShopUser findByChatId(Long id);

}
