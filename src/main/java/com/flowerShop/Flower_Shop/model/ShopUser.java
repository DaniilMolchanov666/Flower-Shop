package com.flowerShop.Flower_Shop.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Entity
@Table(name = "users_from_bot")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@EnableJpaAuditing
public class ShopUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @Column(name = "number_of_phone")
    private String numberOfPhone;

    @NotNull
    @Column(name = "chat_id")
    private long chatId;

    @Column(name = "list_of_requests")
    private String listOfRequests;
}
