package com.flowerShop.mapper;

import com.flowerShop.dto.productDTO.ProductCreateDTO;
import com.flowerShop.dto.productDTO.ProductShowDTO;
import com.flowerShop.dto.productDTO.ProductUpdateDTO;
import com.flowerShop.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.MappingTarget;

@Mapper(
        uses = {Product.class, ProductCreateDTO.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class ProductDTOMapper {

    public abstract ProductShowDTO toProductShowDTO(Product product);

    public abstract Product toProduct(ProductCreateDTO productCreateDTO);

    public abstract void update(ProductUpdateDTO productUpdateDTO, @MappingTarget Product product);
}
