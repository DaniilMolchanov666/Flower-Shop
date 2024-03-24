package com.flowerShop.Flower_Shop.mapper;

import com.flowerShop.Flower_Shop.dto.ProductCreateDTO;
import com.flowerShop.Flower_Shop.dto.ProductShowDTO;
import com.flowerShop.Flower_Shop.model.Product;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-03-24T13:26:12+0300",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.2.jar, environment: Java 17.0.9 (Private Build)"
)
@Component
public class ProductDTOMapperImpl extends ProductDTOMapper {

    @Override
    public ProductShowDTO toProductShowDTO(Product product) {
        if ( product == null ) {
            return null;
        }

        ProductShowDTO productShowDTO = new ProductShowDTO();

        productShowDTO.setName( product.getName() );
        productShowDTO.setCategory( product.getCategory() );
        productShowDTO.setDescription( product.getDescription() );
        productShowDTO.setPrice( product.getPrice() );
        productShowDTO.setPurchasePrice( product.getPurchasePrice() );

        return productShowDTO;
    }

    @Override
    public Product toProduct(ProductCreateDTO productCreateDTO) {
        if ( productCreateDTO == null ) {
            return null;
        }

        Product.ProductBuilder product = Product.builder();

        product.category( productCreateDTO.getCategory() );
        product.name( productCreateDTO.getName() );
        product.description( productCreateDTO.getDescription() );
        product.price( productCreateDTO.getPrice() );
        product.purchasePrice( productCreateDTO.getPurchasePrice() );

        return product.build();
    }
}
