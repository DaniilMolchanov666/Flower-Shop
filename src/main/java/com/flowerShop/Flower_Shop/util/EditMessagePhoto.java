package com.flowerShop.Flower_Shop.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.objects.InputFile;

public class EditMessagePhoto extends EditMessageCaption {

    @JsonProperty("photo")
    private InputFile photo;

    public void setPhoto(InputFile photo) {
        this.photo = photo;
    }
}
