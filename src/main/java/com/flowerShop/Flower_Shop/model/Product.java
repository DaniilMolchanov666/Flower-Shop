package com.flowerShop.Flower_Shop.model;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;

@Getter
@AllArgsConstructor
public class Product {

    private String nameOfProduct;

    private InputFile imageOfProduct;

    private String price;
}
