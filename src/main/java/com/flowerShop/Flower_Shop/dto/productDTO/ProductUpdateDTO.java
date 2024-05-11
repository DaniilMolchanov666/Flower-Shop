package com.flowerShop.Flower_Shop.dto.productDTO;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.DefaultValue;
import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@Builder
public class ProductUpdateDTO {
    @NotEmpty(message = "Категория товара не может быть пустой!")
    private String name;

    @NotEmpty(message = "Имя не может быть пустым!")
    private String category;

    private String description;

    @NotNull(message = "Укажите цену!")
    @Pattern(regexp = "^[0-9]+$", message = "Цена должна быть целым положительным числом!")
    private String price;

    @NotNull(message = "Укажите цену!")
    @Pattern(regexp = "^[0-9]+$", message = "Закупочная цена должна быть целым положительным числом!")
    private String purchasePrice;

    @DefaultValue(value = "без фото.jpg")
    private String nameOfPhoto;
}