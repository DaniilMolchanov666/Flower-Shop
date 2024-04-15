package com.flowerShop.Flower_Shop.repository;

import com.flowerShop.Flower_Shop.model.UserState;
import jakarta.ws.rs.QueryParam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayDeque;

//TODO продумать нужно ли автоматическое удаление записей или простое редактирование одной для каждого юзера
@Repository
public interface UserStateRepository extends CrudRepository<UserState, Integer> {

    ArrayDeque<UserState> findAllByChatId(long id);

    void deleteByChatId(long id);

    //TODO настроить запрос sql для вывода последней записи клиента
//    @Query("SELECT id, chat_id, bot_state, last_viewed_product" +
//            " FROM users_last_states WHERE chat_id = (:id) ORDER BY id DESC LIMIT 1")
//    UserState findLast(@Param("id") Long id);
}
