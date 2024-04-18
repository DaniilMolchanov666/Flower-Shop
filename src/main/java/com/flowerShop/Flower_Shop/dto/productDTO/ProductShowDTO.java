package com.flowerShop.Flower_Shop.dto.productDTO;

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
    private String price;
    private String purchasePrice;
    private String nameOfPhoto;
}
