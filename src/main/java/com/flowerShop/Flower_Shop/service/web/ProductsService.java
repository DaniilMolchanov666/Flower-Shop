package com.flowerShop.Flower_Shop.service.web;

import com.flowerShop.Flower_Shop.model.Product;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ProductsService {

    List<Product> findAll();

    void save(Product product);

    Optional<Product> findProduct(String name);

    void deleteProduct(String name);

    List<Product> findByCategory(String category);

    Optional<Product> findById(int id);


}
