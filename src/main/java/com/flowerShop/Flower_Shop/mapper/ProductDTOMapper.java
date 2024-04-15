package com.flowerShop.Flower_Shop.mapper;

import com.flowerShop.Flower_Shop.dto.productDTO.ProductCreateDTO;
import com.flowerShop.Flower_Shop.dto.productDTO.ProductShowDTO;
import com.flowerShop.Flower_Shop.dto.productDTO.ProductUpdateDTO;
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

    public abstract void update(ProductUpdateDTO productUpdateDTO, @MappingTarget Product product);
}
