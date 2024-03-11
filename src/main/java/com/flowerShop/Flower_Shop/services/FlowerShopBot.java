package com.flowerShop.Flower_Shop.services;

import ch.qos.logback.core.util.FileSize;
import com.flowerShop.Flower_Shop.config.FlowerShopBotConfig;
import com.flowerShop.Flower_Shop.model.FlowerShopBotCommands;
import com.flowerShop.Flower_Shop.model.Product;
import com.flowerShop.Flower_Shop.sender.PhotoForSend;
import com.flowerShop.Flower_Shop.util.EditMessagePhoto;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.print.attribute.standard.MediaName;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.FileNameMap;
import java.util.*;
import java.util.List;

@Slf4j
@Component
public class FlowerShopBot extends TelegramLongPollingBot {

    FlowerShopBotConfig flowerShopBotConfig;

    List<Product> listOfProducts = new ArrayList<>();

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
               new InputFile(new File("src/main/resources/flowers/Букет 1.jpeg")), "3500 рублей"));
        listOfProducts.add(new Product("Букет 2",
                new InputFile(new File("src/main/resources/flowers/Букет 2.jpg")), "4890 рублей"));
        listOfProducts.add(new Product("Букет 3",
                new InputFile(new File("src/main/resources/flowers/Букет 3.jpeg")), "2300 рублей"));
        listOfProducts.add(new Product("Букет 4",
                new InputFile(new File("src/main/resources/flowers/Букет 4.jpeg")), "3600 рублей"));

        try {
            this.execute(new SetMyCommands(listOfCommand, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Commands of bot error!");
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
//        try {
//            Thumbnails.of(Objects.requireNonNull(new File("src/main/resources/flowers")
//                            .listFiles())).size(500, 500)
//                    .outputFormat("JPG")
//                    .outputQuality(0.80)
//                    .toFiles(Rename.NO_CHANGE);
//        } catch (IOException e) {
//            log.error("Ошибка с обработкой изображений!");
//        }

        if (update.hasMessage() && update.getMessage().hasText()) {
            long id = update.getMessage().getChatId();

            String commandMessage = update.getMessage().getText().toLowerCase().trim();

            switch(commandMessage) {
                case "/info":
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(id);
                    sendMessage.setText(FlowerShopBotCommands.INFO.getValue());

                    try {
                        executeAsync(sendMessage);
                    } catch (TelegramApiException e) {
                        log.error("Проблема с выводом команды /info");
                    }
                    break;

                case "/start":

                    SendMediaGroup sendMediaGroup = new SendMediaGroup();
                    List<InputMedia> list = new ArrayList<>();

                    var photo1 = new InputMediaPhoto();
                    photo1.setMedia(new File("src/main/resources/flowers/Букет 1.jpeg"), "kek1");

                    var photo2 = new InputMediaPhoto();
                    photo2.setMedia(new File("src/main/resources/flowers/Букет 2.jpg"), "kek2");

                    list.add(photo1);
                    list.add(photo2);

                    sendMediaGroup.setMedias(list);
                    sendMediaGroup.setChatId(id);

                    SendMessage sendMessage1 = new SendMessage();
                    sendMessage1.setChatId(id);
                    sendMessage1.setText(listOfProducts.get(0).getNameOfProduct()
                            + "\n" + listOfProducts.get(0).getPrice());
                    sendMessage1.setReplyMarkup(getMarkup());
                    try {
                        this.execute(sendMediaGroup);
                        this.execute(sendMessage1);
                    } catch (TelegramApiException e) {
                        log.error("Ошибка с выводом информации о заказе!\n", e.fillInStackTrace());
                    }
                    break;
                default:
                    // do something
            }
        }

        else if (update.hasCallbackQuery()) {
            int messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            var query = update.getCallbackQuery();

            if (query.getData().equals("FORWARD_BUTTON")) {

                var currentProduct = photoSender.getProduct();

                int numberOfNextProduct = listOfProducts.indexOf(currentProduct) == listOfProducts.size() - 1
                        ? listOfProducts.indexOf(currentProduct) : listOfProducts.indexOf(currentProduct) + 1;

                EditMessageMedia editMessagePhoto = new EditMessageMedia();
                var photo = new InputMediaPhoto();
//                photo.setNewMediaFile(listOfProducts.get(listOfProducts.indexOf(currentProduct)).getImageOfProduct());
                photo.setCaption(listOfProducts.get(numberOfNextProduct).getNameOfProduct()
                        + "\n" + listOfProducts.get(numberOfNextProduct).getPrice());
                editMessagePhoto.setMedia(new InputMediaPhoto());
                editMessagePhoto.setChatId(chatId);
                editMessagePhoto.setMessageId(messageId);
//                editMessagePhoto.setCaption(listOfProducts.get(numberOfNextProduct).getNameOfProduct()
//                        + "\n" + listOfProducts.get(numberOfNextProduct).getPrice());
                editMessagePhoto.setReplyMarkup(getMarkup());

                photoSender.setProduct(listOfProducts.get(numberOfNextProduct));

                try {
                    this.execute(editMessagePhoto);
                } catch (TelegramApiException e) {
                    log.error(e.getMessage());
                }

            } else if (query.getData().equals("FORWARD_BACKWARD")) {

            }
        }
    }

//    public ReplyKeyboardMarkup getKeyboard() {
//
//    }

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
        requestButtonShowAll.setText("Показать все " + EmojiParser.parseToUnicode(":cherry_blossom:"));
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
    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);
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
