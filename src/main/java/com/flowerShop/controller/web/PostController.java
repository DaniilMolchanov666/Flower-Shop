package com.flowerShop.controller.web;

import com.flowerShop.dto.productDTO.ProductCreateDTO;
import com.flowerShop.dto.productDTO.ProductUpdateDTO;
import com.flowerShop.mapper.ProductDTOMapper;
import com.flowerShop.service.web.ProductsService;
import com.flowerShop.util.minio.MinioBucketProvider;
import com.flowerShop.util.web.FileManager;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    private final List<String> listOfCategories =
            new ArrayList<>(List.of("Цветы", "Монобукет", "Композиция", "Составной букет", "Другое"));

    @PostMapping("/api/products/create")
    public String addNewProduct(@Valid ProductCreateDTO productCreateDTO,
                                BindingResult bindingResult,
                                @RequestParam MultipartFile file,
                                RedirectAttributes redirectAttributes) throws IOException {
        productCreateDTO.setName(productCreateDTO.getName().trim());
        if (file.isEmpty()) {
            productCreateDTO.setNameOfPhoto("без фото.jpg");
        } else {
            productCreateDTO.setNameOfPhoto(productCreateDTO.getName());
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .toList());
            var listOfCategoriesExcludingSelected = listOfCategories.stream()
                    .filter(i -> !i.equals(redirectAttributes.getFlashAttributes().get("category")))
                    .toList();
            redirectAttributes.addFlashAttribute("categories", listOfCategoriesExcludingSelected);
            redirectAttributes.addFlashAttribute("name", productCreateDTO.getName());
            redirectAttributes.addFlashAttribute("price", productCreateDTO.getPrice());
            redirectAttributes.addFlashAttribute("description", productCreateDTO.getDescription());
            redirectAttributes.addFlashAttribute("category", productCreateDTO.getCategory());
            redirectAttributes.addFlashAttribute("purchase_price", productCreateDTO.getPurchasePrice());
            return "redirect:/api/products/create";
        }

        var newProduct = productDTOMapper.toProduct(productCreateDTO);

        if (!productsService.findAll().contains(newProduct)) {
            redirectAttributes.addFlashAttribute("success",
                    String.format("Товар '%s' добавлен в базу данных!", newProduct.getName()));
            FileManager.createFileAndSaveInDirectory(file.getBytes(), newProduct.getName());
            minioBucketProvider.addFileInBucket(file.getBytes(), newProduct.getName());
            productsService.save(newProduct);
            log.info("Товар {} добавлен в базу данных!", newProduct.getName());
        } else {
            redirectAttributes.addFlashAttribute("error",
                    String.format("Товар с названием '%s' уже существует!", newProduct.getName()));
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
        redirectAttributes.addFlashAttribute("success", String.format("Товар '%s' успешно удален!", name));
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

        productUpdateDTO.setName(productUpdateDTO.getName().trim());

        if (productsService.findProduct(nameOfProduct).isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    "Товар '" + productUpdateDTO.getName() + "' был удален!");
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

        if (!product.getNameOfPhoto().equals("без фото.jpg")) {
            product.setNameOfPhoto(productUpdateDTO.getName());
        }

        if (!file.isEmpty()) {
            minioBucketProvider.deleteFileFromBucket(nameOfProduct);
            minioBucketProvider.addFileInBucket(file.getBytes(), product.getNameOfPhoto());
            FileManager.deleteOldFileAndSaveNew(file.getBytes(), nameOfProduct, product.getNameOfPhoto());
        } else {
            minioBucketProvider.updateFileFromBucket(nameOfProduct, product.getNameOfPhoto());
            FileManager.renameFile(nameOfProduct, product.getNameOfPhoto());
        }

        productsService.save(product);

        redirectAttributes.addFlashAttribute("success",
                String.format("Товар '%s' был обновлен!", product.getName()));
        log.info("Товар {} обновлен в базе данных!", product.getName());
        return "redirect:/api/products";
    }
}
