package com.flowerShop.repository;

import com.flowerShop.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductsRepository extends JpaRepository<Product, Integer> {
    Optional<Product> findByName(String name);

    Optional<Product> findById(Integer id);

    void deleteByName(String name);

    List<Product> findByCategory(String category);
}
