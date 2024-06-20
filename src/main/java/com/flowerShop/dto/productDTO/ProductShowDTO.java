package com.flowerShop.dto.productDTO;

import lombok.Getter;
import lombok.Setter;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Builder;

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
    private String nameOfPhoto;
}
