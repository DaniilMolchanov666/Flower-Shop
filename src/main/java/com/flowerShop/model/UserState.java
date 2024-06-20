package com.flowerShop.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Entity
@Table(name = "users_last_states")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "chatId")
@EnableJpaAuditing
public class UserState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "bot_state")
    @NotNull
    private String botState;

    @Column(name = "chat_id")
    @NotNull
    private long chatId;

    @Column(name = "last_viewed_product")
    private String lastViewedProduct;
}
