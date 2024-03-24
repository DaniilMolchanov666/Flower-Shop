package com.flowerShop.Flower_Shop.service;

import com.flowerShop.Flower_Shop.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ProductsService {

   List<Product> findAll();

    void save(Product product);

    Optional<Product> findProduct(String name);

}
