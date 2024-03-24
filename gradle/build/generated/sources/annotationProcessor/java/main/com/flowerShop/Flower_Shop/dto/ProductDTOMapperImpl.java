package com.flowerShop.Flower_Shop.dto;

import com.flowerShop.Flower_Shop.model.Product;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-03-21T12:52:13+0300",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.2.jar, environment: Java 20.0.1 (Oracle Corporation)"
)
@Component
public class ProductDTOMapperImpl extends ProductDTOMapper {

    @Override
    public ProductShowDTO toProductShowDTO(Product product) {
        if ( product == null ) {
            return null;
        }

        ProductShowDTO productShowDTO = new ProductShowDTO();

        productShowDTO.setPrice( product.getPrice() );

        return productShowDTO;
    }

    @Override
    public Product toProduct(ProductCreateDTO productCreateDTO) {
        if ( productCreateDTO == null ) {
            return null;
        }

        Product product = new Product();

        return product;
    }
}
