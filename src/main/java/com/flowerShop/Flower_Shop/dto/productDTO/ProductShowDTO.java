package com.flowerShop.Flower_Shop.dto.productDTO;

import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@Builder
public class ProductShowDTO {
    private String name;
    private String category;
    private String description;
    private String price;
    private String purchasePrice;
    private Boolean showForBot;
    private String nameOfPhoto;
}
