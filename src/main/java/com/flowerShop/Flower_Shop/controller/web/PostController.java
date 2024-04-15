package com.flowerShop.Flower_Shop.controller.web;

import com.flowerShop.Flower_Shop.dto.productDTO.ProductCreateDTO;
import com.flowerShop.Flower_Shop.dto.productDTO.ProductUpdateDTO;
import com.flowerShop.Flower_Shop.mapper.ProductDTOMapper;
import com.flowerShop.Flower_Shop.service.web.ProductsService;
import com.flowerShop.Flower_Shop.util.web.FileManager;
import jakarta.validation.Valid;
import jakarta.ws.rs.FormParam;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PostController {

    @Autowired
    private ProductsService productsService;

    @Autowired
    private ProductDTOMapper productDTOMapper;

    @PostMapping("/api/products/create")
    public String addNewProduct(@Valid @RequestParam String name,
                                @Valid @RequestParam String category,
                                @Valid @RequestParam String description,
                                @Valid @RequestParam String price,
                                @Valid @RequestParam String purchase_price,
                                @FormParam(value = "file")  MultipartFile file,
                                RedirectAttributes redirectAttributes) {

        var productForCreate = ProductCreateDTO.builder()
                .name(name)
                .category(category)
                .description(description)
                .price(Integer.parseInt(price))
                .purchasePrice(Integer.parseInt(purchase_price))
                .nameOfPhoto(name)
                .build();

        if (file.isEmpty()) {
            productForCreate.setNameOfPhoto("без фото.jpg");
        }

        var product = productDTOMapper.toProduct(productForCreate);

        if (!productsService.findAll().contains(product)) {
            redirectAttributes.addFlashAttribute("success",
                    String.format("Товар %s добавлен в базу данных!", product.getName()));
            FileManager.createFileAndSaveInDirectory(file, name);
            productsService.save(product);
            log.info("Товар {} добавлен в базу данных!", product.getName());
        } else {
            redirectAttributes.addFlashAttribute("error",
                    String.format("Товар с названием %s уже существует!", product.getName()));
            log.info("Товар {} уже существует в базе данных!", product.getName());
        }
        return "redirect:/api/products";
    }

    @PostMapping("/api/products/delete/{name}")
    @SneakyThrows
    public String deleteProduct(@PathVariable(value = "name") String name,
                                RedirectAttributes redirectAttributes) {
        productsService.deleteProduct(name);
        FileManager.deleteFile(name);
        redirectAttributes.addFlashAttribute("success",
                String.format("Товар %s успешно удален!", name));
        log.info("Товар {} удален из базы данных!", name);

        return "redirect:/api/products";
    }

    @PostMapping("/api/products/update/{name}")
    @SneakyThrows
    public String editProduct(@RequestParam String name,
                              @RequestParam String category,
                              @RequestParam String description,
                              @RequestParam String price,
                              @RequestParam String purchase_price,
                              @RequestParam MultipartFile file,
                              @PathVariable(value = "name") String nameOfProduct,
                              Model model,
                              RedirectAttributes redirectAttributes) {

        var product = productsService.findProduct(nameOfProduct).get();

        var productForUpdate = ProductUpdateDTO.builder()
                .name(name)
                .category(category)
                .description(description)
                .price(Integer.parseInt(price))
                .purchasePrice(Integer.parseInt(purchase_price))
                .nameOfPhoto(name)
                .build();

        if (file.isEmpty()) {
            productForUpdate.setNameOfPhoto("без фото.jpg");
        }

        FileManager.deleteOldFileAndSaveNew(file, nameOfProduct, name);

        productDTOMapper.update(productForUpdate, product);
        productsService.save(product);

        redirectAttributes.addFlashAttribute("success",
                String.format("Товар %s был обновлен!", product.getName()));
        log.info("Товар {} обновлен в базе данных!", name);

        return "redirect:/api/products";
    }
}
