package com.flowerShop.Flower_Shop.service.web;

import com.flowerShop.Flower_Shop.model.Product;
import com.flowerShop.Flower_Shop.repository.ProductsRepository;
import lombok.RequiredArgsConstructor;
import org.jvnet.hk2.annotations.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
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

    public Page<Product> findCertainProducts(List<Product> listOfProducts, PageRequest pageable) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;

        List<Product> list;

        if (listOfProducts.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, listOfProducts.size());
            list = listOfProducts.subList(startItem, toIndex);
        }

        return new PageImpl<>(list, PageRequest.of(currentPage, pageSize), listOfProducts.size());
    }

    @Override
    @Transactional
    public void save(Product product) {
        productRepository.save(product);
    }

    @Override
    public Optional<Product> findProduct(String name) {
        return productRepository.findByName(name);
    }

    @Override
    @Transactional
    public void deleteProduct(String name) {
        productRepository.deleteByName(name);
    }

    @Override
    public List<Product> findByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    @Override
    public Optional<Product> findById(int id) {
        return productRepository.findById(id);
    }
}
