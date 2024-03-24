package com.flowerShop.Flower_Shop.controller;

import com.flowerShop.Flower_Shop.dto.ProductCreateDTO;
import com.flowerShop.Flower_Shop.mapper.ProductDTOMapper;
import com.flowerShop.Flower_Shop.dto.ProductShowDTO;
import com.flowerShop.Flower_Shop.model.Product;
import com.flowerShop.Flower_Shop.service.ProductsService;
import jakarta.ws.rs.FormParam;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class    WebController {

    @Autowired
    private ProductsService productsService;

    @Autowired
    private ProductDTOMapper productDTOMapper;

    @GetMapping("/api/products")
    @ResponseStatus(HttpStatus.OK)
    public String showAllProducts(Model model) {
        List<ProductShowDTO> listForSHow = new ArrayList<>();

        productsService.findAll().forEach( i -> {
            listForSHow.add(productDTOMapper.toProductShowDTO(i));
        });
        model.addAttribute("list", listForSHow);
        return "show";
    }

    @PostMapping("/api/products")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ProductCreateDTO> addNewProduct(@RequestBody ProductCreateDTO productCreateDTO) {
       var product = productDTOMapper.toProduct(productCreateDTO);
       productsService.save(product);
       return ResponseEntity.ok().body(productCreateDTO);
    }

    @GetMapping("/resources/static/{name}")
    public String getImage(@PathVariable String name, Model model) {
        var file = new File("/src/main/resources/static/" + name);
        model.addAttribute("image", file);
        return "show";
    }

    @GetMapping("api/products/create")
    public String createNewProduct(@FormParam(value = "product") Product product) {
        return "create";
    }

    @GetMapping("api/products/update/{name}")
    public String upadteProduct(@PathVariable String name, Model model) {
        var product = productsService.findProduct(name);
        model.addAttribute("product", product);
        return "update";
    }
}
