package com.flowerShop.Flower_Shop.repository;

import com.flowerShop.Flower_Shop.model.UserState;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayDeque;

//TODO продумать нужно ли автоматическое удаление записей или простое редактирование одной для каждого юзера
@Repository
public interface UserStateRepository extends CrudRepository<UserState, Integer> {

    ArrayDeque<UserState> findAllByChatId(long id);

    boolean existsByChatId(long id);

    void deleteByChatId(long id);
}
