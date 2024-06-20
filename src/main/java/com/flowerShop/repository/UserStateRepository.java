package com.flowerShop.repository;

import com.flowerShop.model.UserState;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserStateRepository extends CrudRepository<UserState, Integer> {

    UserState findByChatId(long id);

    boolean existsByChatId(long id);

    void deleteByChatId(long id);
}
