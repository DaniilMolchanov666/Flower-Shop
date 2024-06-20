package com.flowerShop.repository;

import com.flowerShop.model.ShopUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<ShopUser, Long> {

    ShopUser findByChatId(Long id);

}
