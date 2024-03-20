package com.flowerShop.Flower_Shop.service;

import com.flowerShop.Flower_Shop.config.FlowerShopBotConfig;
import com.flowerShop.Flower_Shop.controller.UpdateController;
import com.flowerShop.Flower_Shop.model.FlowerShopBotCommands;
import com.flowerShop.Flower_Shop.model.Product;
import com.flowerShop.Flower_Shop.model.User;
import com.flowerShop.Flower_Shop.sender.PhotoForSend;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

@Slf4j
@Component
public class FlowerShopBot extends TelegramLongPollingBot {

    FlowerShopBotConfig flowerShopBotConfig;

    List<Product> listOfProducts = new ArrayList<>();

    List<User> listOfUsers = new ArrayList<>();

    @Autowired
    PhotoForSend photoSender;

    public FlowerShopBot (FlowerShopBotConfig config) {

        this.flowerShopBotConfig = config;

        List<BotCommand> listOfCommand = new ArrayList<>();

        listOfCommand.add(new BotCommand(FlowerShopBotCommands.INFO.getKey(),
                FlowerShopBotCommands.INFO.getValue()));
        listOfCommand.add(new BotCommand(FlowerShopBotCommands.START.getKey(),
                FlowerShopBotCommands.START.getValue()));

        listOfProducts.add(new Product("Букет 1",
               new InputFile(new File("src/main/resources/flowers/Букет 1.jpeg")), 3500));
        listOfProducts.add(new Product("Букет 2",
                new InputFile(new File("src/main/resources/flowers/Букет 2.jpg")), 4890));
        listOfProducts.add(new Product("Букет 3",
                new InputFile(new File("src/main/resources/flowers/Букет 3.jpeg")), 2300));
        listOfProducts.add(new Product("Букет 4",
                new InputFile(new File("src/main/resources/flowers/Букет 4.jpeg")), 3600));

        try {
            this.execute(new SetMyCommands(listOfCommand, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Commands of bot error!");
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            Thumbnails.of(Objects.requireNonNull(new File("src/main/resources/flowers")
                            .listFiles())).size(500, 500)
                    .outputFormat("JPG")
                    .outputQuality(0.80)
                    .toFiles(Rename.NO_CHANGE);
        } catch (IOException e) {
            log.error("Ошибка с обработкой изображений!");
        }

        if (update.hasMessage() && update.getMessage().hasText()) {
            if (listOfUsers.stream().noneMatch(u ->
                    Objects.equals(u.getUpdate().getMessage().getChatId(), update.getMessage().getChatId()))) {
                listOfUsers.add(new User(update.getMessage().getChatId(), update));
            }
            if (!update.getMessage().getText().equals("/start")) {
                if (Character.isDigit(update.getMessage().getText().charAt(0))) {
                   sendInfo(update);
                } else if (Character.isAlphabetic(update.getMessage().getText().charAt(0))) {
                    sendRequestForPhoneNumber(update);
                }
            } else {
                sendStartMessage(update);
            }
        }

        else if (update.hasCallbackQuery()) {

            var currentPressButton = update.getCallbackQuery().getData();

            int numberOfNextProduct;

            var currentProduct = photoSender.getProduct();

            long chatId = update.getCallbackQuery().getMessage().getChatId();

            int messageId = update.getCallbackQuery().getMessage().getMessageId();

            switch(currentPressButton) {
                case "FORWARD_BUTTON":

                    numberOfNextProduct = listOfProducts.indexOf(currentProduct) == listOfProducts.size() - 1
                            ? listOfProducts.indexOf(currentProduct) : listOfProducts.indexOf(currentProduct) + 1;
                    sendPhotoMessage(numberOfNextProduct, update);
                    break;
                case "BACKWARD_BUTTON":

                    numberOfNextProduct = listOfProducts.indexOf(currentProduct) == 0
                            ? listOfProducts.indexOf(currentProduct) : listOfProducts.indexOf(currentProduct) - 1;
                    sendPhotoMessage(numberOfNextProduct, update);
                    break;
                case "REQUEST_BUTTON":

                    long finalChatId = chatId;
                    User user = listOfUsers.stream().filter(u -> u.getChatId() == finalChatId).findAny().get();
                    if (user.getListOfRequests() == null) {
                        List<Product> list = new ArrayList<>();
                        list.add(photoSender.getProduct());
                        user.setListOfRequests(list);
                    } else {
                        List<Product> newList = user.getListOfRequests();
                        newList.add(photoSender.getProduct());
                        user.setListOfRequests(newList);
                    }
                    listOfUsers.add(user);

                    SendMessage sendMessage = new SendMessage();

                    StringBuilder stringBuilder = new StringBuilder();
                    user.getListOfRequests().stream().distinct().forEach(u -> {
                        stringBuilder.append(u.getNameOfProduct() + ": " + u.getPrice() + "\n");
                    });

                    long countPrice = user.getListOfRequests().stream()
                            .mapToInt(Product::getPrice)
                            .sum();

                    sendMessage.setText("Заказ добавлен в корзину!\n" + stringBuilder
                            + "Заказ на сумму: " + countPrice + "\nВыберите следующее действие:");
                    sendMessage.setChatId(chatId);
                    sendMessage.setReplyMarkup(getMarkupForRequest());

                    DeleteMessage deleteMessage = new DeleteMessage();
                    deleteMessage.setChatId(chatId);
                    deleteMessage.setMessageId(messageId);

                    try {
                        this.execute(sendMessage);
                        this.execute(deleteMessage);
                    } catch (TelegramApiException e) {
                        log.error(e.getMessage());
                    }
                    break;

                case "CONTINUE_BUTTON":
                    chatId = update.getCallbackQuery().getMessage().getChatId();
                    messageId = update.getCallbackQuery().getMessage().getMessageId();

                    deleteMessage = new DeleteMessage();
                    deleteMessage.setChatId(chatId);
                    deleteMessage.setMessageId(messageId);

                    try {
                        this.execute(photoSender.getNewSendPhoto());
                        this.execute(deleteMessage);
                    } catch (TelegramApiException e) {
                        log.error(e.getMessage());
                    }
                    break;
                case "END_REQUEST_BUTTON":
                    sendRequestForName(update);
                    break;

            }
        }
    }

    public void sendInfo(Update update) {
        SendMessage sendMessage1 = new SendMessage();
        sendMessage1.setChatId(update.getMessage().getChatId());
        sendMessage1.setText("Готово! Ваш заказ оформлен! Скоро с вами свяжутся для обсуждения доставки!");

        try {
            this.execute(sendMessage1);
        } catch (TelegramApiException e) {
            log.error("Ошибка с выводом формы для заполнения ФИО!\n", e.fillInStackTrace());
        }
    }


    public void sendRequestForName(Update update) {
        SendMessage sendMessage1 = new SendMessage();
        sendMessage1.setChatId(update.getCallbackQuery().getMessage().getChatId());
        sendMessage1.setText("Укажите как к вам обращаться:");

        try {
            this.execute(sendMessage1);
            } catch (TelegramApiException e) {
                log.error("Ошибка с выводом формы для заполнения ФИО!\n", e.fillInStackTrace());
            }
        }

    public void sendRequestForPhoneNumber(Update update) {
        SendMessage sendMessage1 = new SendMessage();
        sendMessage1.setChatId(update.getMessage().getChatId());
        sendMessage1.setText("Укажите ваш номер телефона:");
        try {
            this.execute(sendMessage1);
        } catch (TelegramApiException e) {
            log.error("Ошибка с выводом формы для заполнения номера телефона!\n", e.fillInStackTrace());
        }
    }


    public void sendPhotoMessage(int numberOfNextProduct, Update update) {

        int messageId = update.getCallbackQuery().getMessage().getMessageId();
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        photoSender.setProduct(listOfProducts.get(numberOfNextProduct));
        photoSender.setId(chatId);
        photoSender.setInlineKeyboardMarkup(getMarkup());

        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(messageId);

        try {
            this.execute(photoSender.getNewSendPhoto());
            this.execute(deleteMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    public void sendStartMessage(Update update) {
        long id = update.getMessage().getChatId();

        String commandMessage = update.getMessage().getText().toLowerCase().trim();

        if (commandMessage.equals("/start".toLowerCase().trim())) {

            try {
                photoSender = new PhotoForSend();
                photoSender.setId(id);
                photoSender.setInlineKeyboardMarkup(getMarkup());
                photoSender.setProduct(listOfProducts.get(0));
                this.execute(photoSender.getNewSendPhoto());
            } catch (TelegramApiException e) {
                log.error("Ошибка с выводом информации о заказе!\n", e.fillInStackTrace());
            }
        }
    }

    public InlineKeyboardMarkup getMarkup() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> listRows = new ArrayList<>();

        List<InlineKeyboardButton> listOfButtonsForMove = new ArrayList<>();
        List<InlineKeyboardButton> listOfButtonsForRequest = new ArrayList<>();
        List<InlineKeyboardButton> listOfButtonsForBack = new ArrayList<>();
        List<InlineKeyboardButton> listOfButtonForShowAll = new ArrayList<>();

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
        requestButtonBack.setCallbackData("BACK_BUTTON");

        var requestButtonShowAll = new InlineKeyboardButton();
        requestButtonShowAll.setText("Показать все заказы" + EmojiParser.parseToUnicode(":cherry_blossom:"));
        requestButtonShowAll.setCallbackData("SHOW_ALL_BUTTON");

        listOfButtonsForMove.add(requestButtonBackward);
        listOfButtonsForMove.add(requestButtonForward);
        listOfButtonsForBack.add(requestButtonBack);
        listOfButtonForShowAll.add(requestButtonShowAll);
        listOfButtonsForRequest.add(requestButtonRequest);

        listRows.add(listOfButtonsForMove);
        listRows.add(listOfButtonsForRequest);
        listRows.add(listOfButtonForShowAll);
        listRows.add(listOfButtonsForBack);

        inlineKeyboardMarkup.setKeyboard(listRows);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup getMarkupForRequest() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> listRows = new ArrayList<>();

        List<InlineKeyboardButton> listOfButtonsForRequest = new ArrayList<>();
        List<InlineKeyboardButton> listOfButtonsForContinue = new ArrayList<>();

        var requestButtonContinue= new InlineKeyboardButton();
        requestButtonContinue.setText("Продолжить просмотр" + EmojiParser.parseToUnicode(":shopping_bags:"));
        requestButtonContinue.setCallbackData("CONTINUE_BUTTON");

        var requestButtonForRequest = new InlineKeyboardButton();
        requestButtonForRequest.setText("Оформить заказ" + EmojiParser.parseToUnicode(":package:"));
        requestButtonForRequest.setCallbackData("END_REQUEST_BUTTON");

        listOfButtonsForContinue.add(requestButtonContinue);
        listOfButtonsForRequest.add(requestButtonForRequest);

        listRows.add(listOfButtonsForContinue);
        listRows.add(listOfButtonsForRequest);

        inlineKeyboardMarkup.setKeyboard(listRows);
        return inlineKeyboardMarkup;

    }

    @Override
    public String getBotUsername() {
        return flowerShopBotConfig.getNameOfBot();
    }

    @Override
    public void onRegister() {
        super.onRegister();
    }

    @SuppressWarnings("deprecation")
    @Override
    public String getBotToken() {
        return flowerShopBotConfig.getTokenToAccess();
    }
}
