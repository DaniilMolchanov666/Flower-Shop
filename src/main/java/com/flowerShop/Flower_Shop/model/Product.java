package com.flowerShop.Flower_Shop.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import jakarta.ws.rs.DefaultValue;
import lombok.*;
import org.glassfish.grizzly.http.util.TimeStamp;
import org.mapstruct.MappingTarget;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Entity
@Table(name = "flower_shop_products")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@EnableJpaAuditing
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "category_of_product")
    @NotEmpty(message = "Категория товара не может быть пустой!")
    @Size(min = 2, max = 50, message = "Название категории должно содержать от 2 до 50 символов!")
    @CreatedDate
    @CreatedBy
    private String category;

    @Column
    @NotEmpty(message = "Имя не может быть пустым!")
    @Size(min = 2, max = 50, message = "Название товара должно содержать от 2 до 50 символов!")
    private String name;

    @Column
    @DefaultValue(value = "Без описания")
    private String description;

    @Column
    @NotNull(message = "Укажите цену!")
    @Positive(message = "Указанная цена должна быть больше 0!")
    private int price;

    @Column(name = "purchase_price")
    @Positive
    @DefaultValue(value = "0")
    private int purchasePrice;
}

