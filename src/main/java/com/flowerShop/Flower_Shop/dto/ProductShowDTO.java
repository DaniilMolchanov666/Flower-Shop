package com.flowerShop.Flower_Shop.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class ProductShowDTO {
    private String name;
    private String category;
    private String description;
    private int price;
    private int purchasePrice;
}
