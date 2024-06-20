package com.flowerShop.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import jakarta.ws.rs.DefaultValue;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.io.Serializable;

@Entity
@Table(name = "flower_shop_products")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "name")
@EnableJpaAuditing
@JsonSerialize
public class Product implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "category_of_product")
    @NotEmpty(message = "Категория товара не может быть пустой!")
    @CreatedDate
    @CreatedBy
    private String category;

    @Column
    @NotEmpty(message = "Имя не может быть пустым!")
    private String name;

    @Column
    @DefaultValue(value = "Без описания")
    private String description;

    @Column
    @NotNull(message = "Укажите цену!")
    @Pattern(regexp = "^[0-9]+$", message = "Цена должна быть целым положительным числом!")
    private String price;

    @Column(name = "purchase_price")
    @Pattern(regexp = "^[0-9]+$", message = "Закупочная цена должна быть целым положительным числом!")
    @DefaultValue(value = "0")
    private String purchasePrice;

    @Column(name = "name_of_photo")
    @DefaultValue(value = "без фото.jpg")
    private String nameOfPhoto;
}

