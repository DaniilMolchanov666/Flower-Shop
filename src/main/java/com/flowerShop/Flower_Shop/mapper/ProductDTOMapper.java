package com.flowerShop.Flower_Shop.mapper;

import com.flowerShop.Flower_Shop.dto.ProductCreateDTO;
import com.flowerShop.Flower_Shop.dto.ProductShowDTO;
import com.flowerShop.Flower_Shop.model.Product;
import org.mapstruct.*;

@Mapper(
        uses = {Product.class, ProductCreateDTO.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.WARN
)
public abstract class ProductDTOMapper {

    public abstract ProductShowDTO toProductShowDTO(Product product);

    public abstract Product toProduct(ProductCreateDTO productCreateDTO);
}
