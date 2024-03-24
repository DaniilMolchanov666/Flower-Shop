package com.flowerShop.Flower_Shop.service;

import com.flowerShop.Flower_Shop.model.Product;
import com.flowerShop.Flower_Shop.repository.ProductsRepository;
import jakarta.persistence.Column;
import lombok.RequiredArgsConstructor;
import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Component
@RequiredArgsConstructor
public class ProductsServiceImpl implements ProductsService {

    private final ProductsRepository productRepository;

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    @Transactional
    public void save(Product product) {
        if (productRepository.findByName(product.getName()).isEmpty()) {
            productRepository.save(product);
        }
    }

    @Override
    public Optional<Product> findProduct(String name) {
        return productRepository.findByName(name);
    }
}
