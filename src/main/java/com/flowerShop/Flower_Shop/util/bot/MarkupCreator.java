package com.flowerShop.Flower_Shop.util.bot;

import com.flowerShop.Flower_Shop.model.Product;
import com.vdurmont.emoji.EmojiParser;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//TODO настроить возможность доступа к какждой кнопке для упрощения логики класса и возможности внедрения новых функций
public class MarkupCreator {

    public static InlineKeyboardMarkup getMarkupForCategoryMessage() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        var buttonFlowers = new InlineKeyboardButton();
        buttonFlowers.setText("Цветы\n" + EmojiParser.parseToUnicode(":hibiscus:"));
        buttonFlowers.setCallbackData("Цветы");

        var buttonBouquet = new InlineKeyboardButton();
        buttonBouquet.setText("Монобукет\n" + EmojiParser.parseToUnicode(":ribbon:"));
        buttonBouquet.setCallbackData("Монобукет");

        var compositeBouquetButton = new InlineKeyboardButton();
        compositeBouquetButton.setText("Составной букет\n" + EmojiParser.parseToUnicode(":bouquet:"));
        compositeBouquetButton.setCallbackData("Составной букет");

        var bucketButton = new InlineKeyboardButton();
        bucketButton.setText("Посмотреть корзину\n" + EmojiParser.parseToUnicode(":wastebasket:"));
        bucketButton.setCallbackData("BUCKET_BUTTON");

        List<InlineKeyboardButton> listOfButtonsForMove = new ArrayList<>(List.of(buttonFlowers));
        List<InlineKeyboardButton> listOfButtonsForRequest = new ArrayList<>(List.of(buttonBouquet));
        List<InlineKeyboardButton> listOfButtonsForBack = new ArrayList<>(List.of(compositeBouquetButton));
        List<InlineKeyboardButton> listOfButtonForShowAll = new ArrayList<>(List.of(bucketButton));

        List<List<InlineKeyboardButton>> listRows = new ArrayList<>(List.of(listOfButtonsForMove,
                listOfButtonsForRequest, listOfButtonsForBack, listOfButtonForShowAll));

        inlineKeyboardMarkup.setKeyboard(listRows);
        return inlineKeyboardMarkup;

    }

    public static InlineKeyboardMarkup getBackButton() {
        InlineKeyboardMarkup inlineKeyboardButton = new InlineKeyboardMarkup();
        var requestButtonBack= new InlineKeyboardButton();

        requestButtonBack.setText("Назад");
        requestButtonBack.setCallbackData("BACK_START_BUTTON");

        List<List<InlineKeyboardButton>> listRows = new ArrayList<>();
        List<InlineKeyboardButton> listOfButtonsForMove = new ArrayList<>();

        listOfButtonsForMove.add(requestButtonBack);
        listRows.add(listOfButtonsForMove);

        inlineKeyboardButton.setKeyboard(listRows);
        return inlineKeyboardButton;
    }

    public static InlineKeyboardMarkup getBackMenuButton() {
        InlineKeyboardMarkup inlineKeyboardButton = new InlineKeyboardMarkup();
        var requestButtonBack= new InlineKeyboardButton();

        requestButtonBack.setText("Назад");
        requestButtonBack.setCallbackData("BACK_MENU_BUTTON");

        List<List<InlineKeyboardButton>> listRows = new ArrayList<>();
        List<InlineKeyboardButton> listOfButtonsForMove = new ArrayList<>();

        listOfButtonsForMove.add(requestButtonBack);
        listRows.add(listOfButtonsForMove);

        inlineKeyboardButton.setKeyboard(listRows);
        return inlineKeyboardButton;
    }

    public static InlineKeyboardMarkup getMarkupForMainMenu() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> listRows = new ArrayList<>();

        List<InlineKeyboardButton> listOfButtonsForMove = new ArrayList<>();
        List<InlineKeyboardButton> listOfButtonsForRequest = new ArrayList<>();
        List<InlineKeyboardButton> listOfButtonsForBack = new ArrayList<>();

        var requestButtonForward = new InlineKeyboardButton();
        requestButtonForward.setText(EmojiParser.parseToUnicode(":arrow_forward:"));
        requestButtonForward.setCallbackData("FORWARD_BUTTON");

        var requestButtonBackward = new InlineKeyboardButton();
        requestButtonBackward.setText(EmojiParser.parseToUnicode(":arrow_backward:"));
        requestButtonBackward.setCallbackData("BACKWARD_BUTTON");

        var requestButtonRequest = new InlineKeyboardButton();
        requestButtonRequest.setText("Заказать " + EmojiParser.parseToUnicode(":heart:"));
        requestButtonRequest.setCallbackData("REQUEST_BUTTON");

        var requestButtonBack= new InlineKeyboardButton();
        requestButtonBack.setText("Назад");
        requestButtonBack.setCallbackData("BACK_CATEGORIES_BUTTON");

        listOfButtonsForMove.add(requestButtonBackward);
        listOfButtonsForMove.add(requestButtonForward);
        listOfButtonsForBack.add(requestButtonBack);
        listOfButtonsForRequest.add(requestButtonRequest);

        listRows.add(listOfButtonsForMove);
        listRows.add(listOfButtonsForRequest);
        listRows.add(listOfButtonsForBack);

        inlineKeyboardMarkup.setKeyboard(listRows);
        return inlineKeyboardMarkup;
    }

    public static InlineKeyboardMarkup getMarkupForCheckDataOfUserMenu() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> listRows = new ArrayList<>();

        List<InlineKeyboardButton> listOfButtonsForUpdateName = new ArrayList<>();
        List<InlineKeyboardButton> listOfButtonsForUpdatePhoneNumber = new ArrayList<>();
        List<InlineKeyboardButton> listOfButtonsForRequest = new ArrayList<>();

        var buttonForName= new InlineKeyboardButton();
        buttonForName.setText("Поменять имя" + EmojiParser.parseToUnicode(":writing_hand:"));
        buttonForName.setCallbackData("UPDATE_NAME_BUTTON");

        var buttonForPhone= new InlineKeyboardButton();
        buttonForPhone.setText("Поменять номер телефона" + EmojiParser.parseToUnicode(":telephone:"));
        buttonForPhone.setCallbackData("UPDATE_PHONE_BUTTON");

        var requestButtonForRequest = new InlineKeyboardButton();
        requestButtonForRequest.setText("Да, все верно!" + EmojiParser.parseToUnicode(":v:"));
        requestButtonForRequest.setCallbackData("SEND_REQUEST_BUTTON");

        listOfButtonsForUpdateName.add(buttonForName);
        listOfButtonsForUpdatePhoneNumber.add(buttonForPhone);
        listOfButtonsForRequest.add((requestButtonForRequest));

        listRows.add(listOfButtonsForUpdateName);
        listRows.add(listOfButtonsForUpdatePhoneNumber);
        listRows.add(listOfButtonsForRequest);

        inlineKeyboardMarkup.setKeyboard(listRows);
        return inlineKeyboardMarkup;
    }

    public static InlineKeyboardMarkup getDeleteProductMenu(Optional<Product> p) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> listRows = new ArrayList<>();

        List<InlineKeyboardButton> listOfDeleteButton = new ArrayList<>();
        List<InlineKeyboardButton> listOfBackButton = new ArrayList<>();

        var buttonForDelete= new InlineKeyboardButton();
        buttonForDelete.setText("Удалить из корзины" + EmojiParser.parseToUnicode(":scissors:"));
        buttonForDelete.setCallbackData("DELETE_BUTTON" + p.map(Product::getName).orElse(""));

        var buttonForBack= new InlineKeyboardButton();
        buttonForBack.setText("Вернуться в корзину" + EmojiParser.parseToUnicode(":arrow_left:"));
        buttonForBack.setCallbackData("BACK_TO_BUCKET_BUTTON");

        listOfBackButton.add(buttonForBack);
        listOfDeleteButton.add(buttonForDelete);

        listRows.add(listOfDeleteButton);
        listRows.add(listOfBackButton);

        inlineKeyboardMarkup.setKeyboard(listRows);
        return inlineKeyboardMarkup;
    }

    public static InlineKeyboardMarkup getButtonsForALlRequestsInBucket(List<Product> list) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> listRows = new ArrayList<>();
        for (Product product: list) {
            List<InlineKeyboardButton> productButton = new ArrayList<>();
            var buttonForName= new InlineKeyboardButton();
            buttonForName.setText(product.getName() + " : " + product.getPrice() + " р.");
            buttonForName.setCallbackData(product.getName());
            productButton.add(buttonForName);
            listRows.add(productButton);
        }
        List<InlineKeyboardButton> requestButton = new ArrayList<>();
        List<InlineKeyboardButton> letterButton = new ArrayList<>();
        List<InlineKeyboardButton> buttonForDeleteBucketList = new ArrayList<>();
        List<InlineKeyboardButton> deleteButtonList = new ArrayList<>();

        var buttonForRequest= new InlineKeyboardButton();
        buttonForRequest.setText("Заказать" + EmojiParser.parseToUnicode(":white_check_mark:"));
        buttonForRequest.setCallbackData("END_REQUEST_BUTTON");

        var buttonForPostcard= new InlineKeyboardButton();
        buttonForPostcard.setText("Написать текст для открытки" + EmojiParser.parseToUnicode(":love_letter:"));
        buttonForPostcard.setCallbackData("LETTER_BUTTON");

        var buttonForDeleteBucket= new InlineKeyboardButton();
        buttonForDeleteBucket.setText("Очистить корзину" + EmojiParser.parseToUnicode(":wastebasket:"));
        buttonForDeleteBucket.setCallbackData("DELETE_BUTTON");

        var deleteButton= new InlineKeyboardButton();
        deleteButton.setText("Назад" + EmojiParser.parseToUnicode(":arrow_left:"));
        deleteButton.setCallbackData("CONTINUE_BUTTON");

        requestButton.add(buttonForRequest);
        letterButton.add(buttonForPostcard);
        deleteButtonList.add(deleteButton);
        buttonForDeleteBucketList.add(buttonForDeleteBucket);

        listRows.add(requestButton);
        listRows.add(letterButton);
        listRows.add(buttonForDeleteBucketList);
        listRows.add(deleteButtonList);

        inlineKeyboardMarkup.setKeyboard(listRows);

        return inlineKeyboardMarkup;
    }

    public static InlineKeyboardMarkup getStartMenu() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        var categoryButton= new InlineKeyboardButton();
        categoryButton.setText("Посмотреть товары ");
        categoryButton.setCallbackData("CATEGORY_BUTTON");

        var helpButton= new InlineKeyboardButton();
        helpButton.setText("Связь с продавцом ");
        helpButton.setCallbackData("HELP_BUTTON");

        List<InlineKeyboardButton> firstRowList =
                new ArrayList<>(List.of(categoryButton, helpButton));

        List<List<InlineKeyboardButton>> listRows = new ArrayList<>(List.of(firstRowList));
        inlineKeyboardMarkup.setKeyboard(listRows);
        return inlineKeyboardMarkup;
    }
}
