package com.flowerShop.mapper;

import com.flowerShop.dto.productDTO.ProductCreateDTO;
import com.flowerShop.dto.productDTO.ProductShowDTO;
import com.flowerShop.dto.productDTO.ProductUpdateDTO;
import com.flowerShop.model.Product;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-05-31T12:48:38+0300",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.2.jar, environment: Java 17.0.9 (Private Build)"
)
@Component
public class ProductDTOMapperImpl extends ProductDTOMapper {

    @Override
    public ProductShowDTO toProductShowDTO(Product product) {
        if ( product == null ) {
            return null;
        }

        ProductShowDTO.ProductShowDTOBuilder productShowDTO = ProductShowDTO.builder();

        productShowDTO.name( product.getName() );
        productShowDTO.category( product.getCategory() );
        productShowDTO.description( product.getDescription() );
        productShowDTO.price( product.getPrice() );
        productShowDTO.purchasePrice( product.getPurchasePrice() );
        productShowDTO.nameOfPhoto( product.getNameOfPhoto() );

        return productShowDTO.build();
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
        product.nameOfPhoto( productCreateDTO.getNameOfPhoto() );

        return product.build();
    }

    @Override
    public void update(ProductUpdateDTO productUpdateDTO, Product product) {
        if ( productUpdateDTO == null ) {
            return;
        }

        if ( productUpdateDTO.getCategory() != null ) {
            product.setCategory( productUpdateDTO.getCategory() );
        }
        if ( productUpdateDTO.getName() != null ) {
            product.setName( productUpdateDTO.getName() );
        }
        if ( productUpdateDTO.getDescription() != null ) {
            product.setDescription( productUpdateDTO.getDescription() );
        }
        if ( productUpdateDTO.getPrice() != null ) {
            product.setPrice( productUpdateDTO.getPrice() );
        }
        if ( productUpdateDTO.getPurchasePrice() != null ) {
            product.setPurchasePrice( productUpdateDTO.getPurchasePrice() );
        }
        if ( productUpdateDTO.getNameOfPhoto() != null ) {
            product.setNameOfPhoto( productUpdateDTO.getNameOfPhoto() );
        }
    }
}
