package com.flowerShop.Flower_Shop.dto.productDTO;

import jakarta.persistence.Column;
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
public class ProductCreateDTO {
    @NotEmpty(message = "Имя не может быть пустым!")
    private String name;

    @NotEmpty(message = "Категория товара не может быть пустой!")
    private String category;

    @DefaultValue(value = "Без описания")
    private String description;

    @NotNull(message = "Укажите цену!")
    @Pattern(regexp = "^[0-9]+$", message = "Цена должна быть целым положительным числом!")
    private String price;

    @Column(name = "purchase_price")
    @Pattern(regexp = "^[0-9]+$", message = "Закупочная цена должна быть целым положительным числом!")
    @DefaultValue(value = "0")
    private String purchasePrice;

    @Column(name = "show_for_bot")
    @DefaultValue(value = "true")
    private boolean showForBot;

    @DefaultValue(value = "без фото.jpg")
    private String nameOfPhoto;
}
