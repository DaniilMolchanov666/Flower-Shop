package com.flowerShop.Flower_Shop.dto.productDTO;

import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@Builder
public class ProductCreateDTO {
    private String name;
    private String category;
    private String description;
    private int price;
    private int purchasePrice;
    private String nameOfPhoto;
}
