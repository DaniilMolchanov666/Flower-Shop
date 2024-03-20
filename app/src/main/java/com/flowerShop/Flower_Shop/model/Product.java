package com.flowerShop.Flower_Shop.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@Entity
//@Table(name = "products")
public class Product {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private long id;

//    @Column(name = "name")
//    @NotNull
    private String nameOfProduct;

//    @Column(name = "name")
//    @NotNull
    private InputFile imageOfProduct;

    private int price;
}
