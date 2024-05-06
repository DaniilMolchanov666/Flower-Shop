package com.flowerShop.Flower_Shop.controller.web;

import com.flowerShop.Flower_Shop.dto.productDTO.ProductCreateDTO;
import com.flowerShop.Flower_Shop.dto.productDTO.ProductUpdateDTO;
import com.flowerShop.Flower_Shop.mapper.ProductDTOMapper;
import com.flowerShop.Flower_Shop.model.Product;
import com.flowerShop.Flower_Shop.service.web.ProductsService;
import com.flowerShop.Flower_Shop.util.minio.MinioBucketProvider;
import com.flowerShop.Flower_Shop.util.web.FileManager;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PostController {

    @Autowired
    private ProductsService productsService;

    @Autowired
    private ProductDTOMapper productDTOMapper;

    @Autowired
    private MinioBucketProvider minioBucketProvider;

    @PostMapping("/api/products/create")
    public String addNewProduct(@Valid ProductCreateDTO productCreateDTO,
                                BindingResult bindingResult,
                                @RequestParam MultipartFile file,
                                RedirectAttributes redirectAttributes) throws IOException {
        if (file.isEmpty()) {
            productCreateDTO.setNameOfPhoto("без фото.jpg");
        } else {
            productCreateDTO.setNameOfPhoto(productCreateDTO.getName());
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .toList());
            return "redirect:/api/products/create";
        }

        var newProduct = productDTOMapper.toProduct(productCreateDTO);

        if (!productsService.findAll().contains(newProduct)) {
            redirectAttributes.addFlashAttribute("success",
                    String.format("Товар %s добавлен в базу данных!", newProduct.getName()));
            FileManager.createFileAndSaveInDirectory(file, newProduct.getName());
            minioBucketProvider.addFileInBucket(file.getBytes(), newProduct.getName());
            productsService.save(newProduct);
            log.info("Товар {} добавлен в базу данных!", newProduct.getName());
        } else {
            redirectAttributes.addFlashAttribute("error",
                    String.format("Товар с названием %s уже существует!", newProduct.getName()));
            log.info("Товар {} уже существует в базе данных!", newProduct.getName());
        }
        return "redirect:/api/products";
    }

    @PostMapping("/api/products/delete/{name}")
    @SneakyThrows
    public String deleteProduct(@PathVariable(value = "name") String name,
                                RedirectAttributes redirectAttributes) {
        productsService.deleteProduct(name);
        FileManager.deleteFile(name);
        minioBucketProvider.deleteFileFromBucket(name);
        redirectAttributes.addFlashAttribute("success",
                String.format("Товар %s успешно удален!", name));
        log.info("Товар {} удален из базы данных!", name);
        return "redirect:/api/products";
    }

    @PostMapping("/api/products/update/{name}")
    @SneakyThrows
    public String editProduct(@Valid ProductUpdateDTO productUpdateDTO,
                              BindingResult bindingResult,
                              @RequestParam MultipartFile file,
                              RedirectAttributes redirectAttributes,
                              @PathVariable(value = "name") String nameOfProduct) {

        if (productsService.findProduct(nameOfProduct).isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    "Товар '" + nameOfProduct + "' был удален!");
            return "redirect:/api/products";
        }

        var product = productsService.findProduct(nameOfProduct).get();

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .toList());
            return "redirect:/api/products/update/" + nameOfProduct;
        }

        productDTOMapper.update(productUpdateDTO, product);

        if (file.isEmpty()) {
            product.setNameOfPhoto("без фото.jpg");
            minioBucketProvider.deleteFileFromBucket(nameOfProduct);
        } else {
            product.setNameOfPhoto(productUpdateDTO.getName());
            minioBucketProvider.addFileInBucket(file.getBytes(), productUpdateDTO.getName());
        }

        FileManager.deleteOldFileAndSaveNew(file, nameOfProduct, productUpdateDTO.getName());
        productsService.save(product);

        redirectAttributes.addFlashAttribute("success",
                String.format("Товар %s был обновлен!", product.getName()));
        log.info("Товар {} обновлен в базе данных!", product.getName());

        return "redirect:/api/products";
    }
}
