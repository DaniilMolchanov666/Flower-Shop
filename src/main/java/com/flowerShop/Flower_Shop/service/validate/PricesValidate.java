package com.flowerShop.Flower_Shop.service.validate;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class PricesValidate implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return false;
    }

    @Override
    public void validate(Object target, Errors errors) {
        String s = (String) target;
        for (char c: s.toCharArray()) {
            if (!Character.isDigit(c)) {

            }
        }
    }
}
