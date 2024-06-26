package com.flowerShop.controller.web;

import com.flowerShop.mapper.ProductDTOMapper;
import com.flowerShop.dto.productDTO.ProductShowDTO;
import com.flowerShop.model.Product;
import com.flowerShop.service.web.ProductsServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.IntStream;

@Controller
@RequiredArgsConstructor
@Slf4j
@Api
public class GetController {

    @Autowired
    private ProductsServiceImpl productsService;

    @Autowired
    private ProductDTOMapper productDTOMapper;

    private final List<Integer> listOfSizes = new ArrayList<>(List.of(10, 15, 25));

    private final List<String> listOfCategories =
            new ArrayList<>(List.of("Цветы", "Монобукет", "Композиция", "Составной букет", "Другое"));

    @GetMapping("/api/products")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation("Вывод всех продуктов")
    public String showAllProducts(@RequestParam(value = "page", required = false, defaultValue = "1")
                                  int pageNumber,
                                  @RequestParam(value = "size", required = false, defaultValue = "10")
                                  int sizeNumber,
                                  @RequestParam(value = "category", required = false, defaultValue = "all")
                                  String category,
                                  @RequestParam(value = "orderBy", required = false, defaultValue = "price")
                                  String orderBy,
                                  @RequestParam(value = "name", required = false) String name,
                                  Model model, RedirectAttributes redirectAttributes) {
        if (!redirectAttributes.getFlashAttributes().isEmpty()) {
            model.addAttribute(orderBy, redirectAttributes.getFlashAttributes());
        }

        List<Product> listOfProducts = new ArrayList<>();
        if (!category.equals("all")) {
            listOfProducts
                    .addAll(productsService
                            .findByCategory(category));
        } else {
            listOfProducts.addAll(productsService.findAll());
        }

        if (name != null) {
            listOfProducts = listOfProducts.stream()
                    .filter(i -> i.getName().trim().contains(name))
                    .toList();
        }

        List<ProductShowDTO> listForSHow = new ArrayList<>();

        Page<Product> page = productsService.findCertainProducts(listOfProducts,
                PageRequest.of(pageNumber - 1, sizeNumber));

        page.forEach(p -> listForSHow.add(productDTOMapper.toProductShowDTO(p)));

        switch (orderBy) {
            case "price":
                listForSHow.sort(Comparator.comparing(ProductShowDTO::getPrice));
                break;
            case "purchasePrice":
                listForSHow.sort(Comparator.comparing(ProductShowDTO::getPurchasePrice));
                break;
            case "name":
                listForSHow.sort(Comparator.comparing(ProductShowDTO::getName));
                break;
        }

        model.addAttribute("sizes", listOfSizes.stream().filter(i -> i != sizeNumber).toList());
        model.addAttribute("page", page);
        model.addAttribute("listOfProducts", listForSHow);
        model.addAttribute("countOfProducts", listOfProducts.size());
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("size", sizeNumber);

        if (page.getTotalPages() > 0) {
            List<Integer> listOfPages = IntStream.rangeClosed(1, page.getTotalPages())
                    .boxed()
                    .toList();
            model.addAttribute("listOfPages", listOfPages);
        }
        return "show";
    }

    @GetMapping("api/products/create")
    public String createNewProduct(RedirectAttributes redirectAttributes) {

        return "create";
    }

    @GetMapping("/api/products/delete/{name}")
    public String chooseProductForDelete(@PathVariable String name, Model model) {
        model.addAttribute("name", name);
        return "delete";
    }

    @GetMapping("/api/products/update/{name}")
    public String chooseProductForEdit(@PathVariable String name,
                                       RedirectAttributes redirectAttributes,
                                       Model model) {
        if (productsService.findProduct(name).isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    "Товар '" + name + "' был удален!");
            return "redirect:/api/products";
        }
        var product = productsService.findProduct(name).get();
        var listOfCategoriesExcludingSelected = listOfCategories.stream()
                .filter(i -> !i.equals(product.getCategory()))
                .toList();
        model.addAttribute("name", product.getName());
        model.addAttribute("categories", listOfCategoriesExcludingSelected);
        model.addAttribute("category", product.getCategory());
        model.addAttribute("description", product.getDescription());
        model.addAttribute("price", product.getPrice());
        model.addAttribute("purchase_price", product.getPurchasePrice());
        return "update";
    }

    @RequestMapping(value = "/imageDisplay", method = RequestMethod.GET)
    public void showImageForProductOnPage(@RequestParam(value = "nameOfPhoto", required = false) String name,
                                          HttpServletResponse response,
                                          HttpServletRequest request) {

        String PATH_FOR_FLOWERS = "./flowers/";
        Path path = Paths.get(PATH_FOR_FLOWERS + name);
        byte[] file = null;
        try {
            file = Files.readAllBytes(path);
        } catch (IOException | NullPointerException e) {
            log.error("Фото для товара {} не было загружено!", name);
        }
        response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
        try {
            response.getOutputStream().write(file);
            response.getOutputStream().close();
        } catch (IOException | NullPointerException e) {
            log.error("Не удалось вывести файл {} потому что его не существует ({})!",
                    name, e.getMessage());
        }
    }
}
