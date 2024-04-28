package com.flowerShop.Flower_Shop.repository;

import com.flowerShop.Flower_Shop.model.UserState;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserStateRepository extends CrudRepository<UserState, Integer> {

    UserState findByChatId(long id);

    boolean existsByChatId(long id);

    void deleteByChatId(long id);
}
