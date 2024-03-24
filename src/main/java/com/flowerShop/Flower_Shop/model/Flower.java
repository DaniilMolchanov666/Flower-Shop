package com.flowerShop.Flower_Shop.model;

import lombok.*;
import org.telegram.telegrambots.meta.api.objects.InputFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@Entity
//@Table(name = "products")
public class Flower {

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
