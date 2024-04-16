package com.flowerShop.Flower_Shop.service.bot;

import com.flowerShop.Flower_Shop.config.FlowerShopBotConfig;
import com.flowerShop.Flower_Shop.mapper.ProductDTOMapper;
import com.flowerShop.Flower_Shop.model.Product;
import com.flowerShop.Flower_Shop.model.ShopUser;
import com.flowerShop.Flower_Shop.model.UserState;
import com.flowerShop.Flower_Shop.sender.MultiContentMessageSender;
import com.flowerShop.Flower_Shop.sender.PhotoSender;
import com.flowerShop.Flower_Shop.sender.TextMessageSender;
import com.flowerShop.Flower_Shop.service.bot.user_service.UserServiceImpl;
import com.flowerShop.Flower_Shop.service.bot.user_state_service.UserStateService;
import com.flowerShop.Flower_Shop.util.bot.FlowerShopBotCommands;
import com.flowerShop.Flower_Shop.service.web.ProductsServiceImpl;
import com.flowerShop.Flower_Shop.util.bot.UpdateState;
import com.flowerShop.Flower_Shop.util.bot.MarkupCreator;
import com.flowerShop.Flower_Shop.util.bot.UserMessageProvider;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

//TODO настроить кэш для вывода фотографий в боте
@Slf4j
@SpringBootApplication
@EnableCaching
public class FlowerShopBot extends TelegramLongPollingBot {

    FlowerShopBotConfig flowerShopBotConfig;

    @Autowired
    ProductsServiceImpl productsService;

    @Autowired
    UserServiceImpl userService;

    @Autowired
    UserStateService userStateService;

    private static final long id_admin1 = 1402556700L;

    private static final long id_admin2 = 6831132148L;

    @SuppressWarnings("deprecation")
    public FlowerShopBot (FlowerShopBotConfig config) {

        this.flowerShopBotConfig = config;

        List<BotCommand> listOfCommand = new ArrayList<>();

        listOfCommand.add(new BotCommand(FlowerShopBotCommands.START.getKey(),
                FlowerShopBotCommands.START.getValue()));
        try {
            this.execute(new SetMyCommands(listOfCommand, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Commands of bot error!");
        }
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            if (userService.findUser(chatId) == null) {
                ShopUser shopUser = ShopUser.builder().chatId(chatId).build();
                userService.save(shopUser);
            }
            startCommandCheck(update);
        } else if (update.hasCallbackQuery()) {
            buttonsCheck(update);
        }
    }

    //TODO настроить грамотное удаление и устранить неправильную работу в случае отсуствия продукта при его открытии в корзи
    public void buttonsCheck(Update update) throws TelegramApiException {
        String currentButton = update.getCallbackQuery().getData();
        long id = update.getCallbackQuery().getFrom().getId();
        UserState userState;
        Product product;
        int index = 0;
        switch (currentButton) {
            case "Цветы", "Монобукет", "Составной букет":
                if (productsService.findByCategory(currentButton).isEmpty()) {
                    setStateForUserAndSendProduct(update, Optional.empty(), UpdateState.FLOWERS.name());
                } else {
                    setStateForUserAndSendProduct(update,
                            Optional.of(productsService.findByCategory(currentButton).get(index)),
                            UpdateState.FLOWERS.name());
                }
                break;
            case "CATEGORY_BUTTON", "BACK_CATEGORIES_BUTTON", "BACK_MENU_BUTTON":
                sendChooseCategoryMenu(id);
                break;
            case "BACK_START_BUTTON":
                sendStartMenu(id);
                break;
            case "FORWARD_BUTTON", "BACKWARD_BUTTON":
                product = userStateService.getLasViewedProductOfUser(id).get();
                List<Product> list = productsService.findByCategory(product.getCategory());
                if (currentButton.equals("FORWARD_BUTTON")) {
                    index = list.indexOf(product) >= list.size() - 1
                            ? list.indexOf(product) : list.indexOf(product) + 1;
                } else {
                    index = list.indexOf(product) <= 0
                            ? list.indexOf(product) : list.indexOf(product) - 1;
                }
                setStateForUserAndSendProduct(update, Optional.of(list.get(index)), UpdateState.FLOWERS.name());
                break;
            case "REQUEST_BUTTON", "DELETE_BUTTON":
                var userServiceUser = userService.findUser(id);
                Optional<Product> lastProduct = userStateService.getLasViewedProductOfUser(id);

                if (currentButton.equals("DELETE_BUTTON")) {
                    userServiceUser  = userService.findUser(id);
                    userServiceUser.setListOfRequests(null);
                    userService.save(userServiceUser);
                    sendChooseCategoryMenu(id);
                    return;
                }

                getBucket(update, userServiceUser, lastProduct, id);
                break;
            case "BACK_TO_BUCKET_BUTTON", "BUCKET_BUTTON":
                userServiceUser = userService.findUser(id);
                getBucket(update, userServiceUser, Optional.empty(), id);
                break;
            case "CONTINUE_BUTTON":
                userState = userStateService.findAllByChatId(id).getLast();
                var product1 = productsService
                        .findById(Integer.parseInt(userState.getLastViewedProduct()))
                        .orElse(null);
                setStateForUserAndSendProduct(update, Optional.ofNullable(product1), UpdateState.FLOWERS.name());
                break;
            case "END_REQUEST_BUTTON":
                if (userService.findUser(id).getName() != null
                        && userService.findUser(id).getNumberOfPhone() != null) {
                    sendSuggestionToCheckData(userService.findUser(id), update);
                } else {
                    userStateService.setState(id, Optional.empty(), UpdateState.NAME_REQUEST.name());
                    this.executeAsync(TextMessageSender.sendInfo(id, "Укажите как к вам обращаться:"));
                    this.executeAsync(PhotoSender.deleteMessage(update));
                    startCommandCheck(update);
                }
                break;
            case "UPDATE_NAME_BUTTON":
                userStateService.setState(id, Optional.empty(), UpdateState.UPDATE_NAME.name());
                this.executeAsync(TextMessageSender.sendInfo(id, "Укажите как к вам обращаться:"));
                this.executeAsync(PhotoSender.deleteMessage(update));
                startCommandCheck(update);
                break;
            case "UPDATE_PHONE_BUTTON":
                userStateService.setState(id, Optional.empty(), UpdateState.UPDATE_PHONE.name());
                this.executeAsync(TextMessageSender.sendInfo(id, "Укажите свой номер телефона:"));
                this.executeAsync(PhotoSender.deleteMessage(update));
                startCommandCheck(update);
                break;
            case "SEND_REQUEST_BUTTON":
                userStateService.setState(id, Optional.empty(), UpdateState.SUCCESS_STATE.name());
                sendRequestForAdmin(userService.findUser(id), update);
                break;
            case "HELP_BUTTON":
                String helpMessage = "По всем вопросам можете обращаться к ...";
                this.executeAsync(MultiContentMessageSender.sendMessage(id,
                        helpMessage, MarkupCreator.getBackButton()));
                this.executeAsync(MultiContentMessageSender.deleteMessage(update));
                break;
            default:
                if (!currentButton.contains("DELETE_PRODUCT_BUTTON")) {
                    Optional<Product> currentProduct = productsService.findProduct(update.getCallbackQuery().getData());
                    if (currentProduct.isPresent()) {
                        this.executeAsync(PhotoSender.sendMessage(update, currentProduct.get(),
                                MarkupCreator.getDeleteProductMenu()));
                        this.executeAsync(PhotoSender.deleteMessage(update));
                    } else {
                        setStateForUserAndSendProduct(update, Optional.empty(), UpdateState.BUCKET_STATE.name());
                    }
                } else {
                    List<Product> listOfProductsExcludeDeleted = new ArrayList<>();
                    List<String> arrayOfProducts = Arrays.stream(userService.findUser(id).getListOfRequests()
                            .split(",")).toList();

                    var lastViewedProduct = userStateService.getLasViewedProductOfUser(id);
                    boolean isDeleted = false;
                    for (String currentProduct: arrayOfProducts) {
                        if (lastViewedProduct.isPresent() &&
                                Objects.equals(currentProduct, lastViewedProduct.get().getName())) {
                            if (!isDeleted) {
                                isDeleted = true;
                                continue;
                            }
                        }
                        listOfProductsExcludeDeleted.add(productsService.findProduct(currentProduct).orElse(null));
                    }
                    listOfProductsExcludeDeleted.removeIf(Objects::isNull);
                    var u = userService.findUser(id);
                    u.setListOfRequests(listOfProductsExcludeDeleted.stream()
                            .map(Product::getName)
                            .collect(Collectors.joining(",")));
                    userService.save(u);

                    if (listOfProductsExcludeDeleted.isEmpty()) {
                        this.executeAsync(MultiContentMessageSender.sendMessage(id, "Ваша корзина пуста!",
                                MarkupCreator.getBackMenuButton()));
                        this.executeAsync(MultiContentMessageSender.deleteMessage(update));
                    } else {
                        this.executeAsync(MultiContentMessageSender
                                .sendMessage(id, "Ваша корзина:\n",
                                        MarkupCreator.getButtonsForALlRequestsInBucket(listOfProductsExcludeDeleted)));
                        this.executeAsync(MultiContentMessageSender.deleteMessage(update));
                    }
                }
                break;
        }
    }

    private void getBucket(Update update, ShopUser userOfBot, Optional<Product> lastProduct, long id) throws TelegramApiException {

        String requests = userOfBot.getListOfRequests() != null ? userOfBot.getListOfRequests() : "";

        List<Product> listOfRequests = new ArrayList<>(Arrays.stream(requests.split(","))
                .map(i -> productsService.findProduct(i).orElse(null))
                .toList());
        listOfRequests.add(lastProduct.orElse(null));
        listOfRequests.removeIf(Objects::isNull);

        userOfBot.setListOfRequests(listOfRequests.stream().map(Product::getName).collect(Collectors.joining(",")));
        userService.save(userOfBot);

        if (listOfRequests.isEmpty()) {
            this.executeAsync(MultiContentMessageSender.sendMessage(id, "Ваша корзина пуста!",
                    MarkupCreator.getBackMenuButton()));
            this.executeAsync(MultiContentMessageSender.deleteMessage(update));
        } else {
            this.executeAsync(MultiContentMessageSender
                    .sendMessage(id, "Ваша корзина:\n",
                            MarkupCreator.getButtonsForALlRequestsInBucket(listOfRequests)));
            this.executeAsync(MultiContentMessageSender.deleteMessage(update));
        }
    }

    public void setStateForUserAndSendProduct(Update update, Optional<Product> product, String state) throws TelegramApiException {
        long id = update.getCallbackQuery().getFrom().getId();
        if (product.isPresent()) {
            var list = productsService.findByCategory(product.get().getCategory());
            var photoSender = PhotoSender.sendMessage(update, product.get(), MarkupCreator.getMarkupForMainMenu());
            photoSender.setCaption("%s\n( %d из %d )".formatted(photoSender.getCaption(),
                        list.indexOf(product.get()) + 1, list.size()));

            this.executeAsync(photoSender);
            this.executeAsync(PhotoSender.deleteMessage(update));

            userStateService.setState(id, product, state);
        } else {
            this.executeAsync(MultiContentMessageSender.sendMessage(id,
                    UserMessageProvider.infoIfCategoryIsNull,
                    MarkupCreator.getBackMenuButton()));
            this.executeAsync(PhotoSender.deleteMessage(update));
        }
    }

    public void sendStartMenu(long id) throws TelegramApiException {
        String greeting = "Добро пожаловать! Что вас интересует?";
        this.executeAsync(MultiContentMessageSender.sendMessage(id, greeting, MarkupCreator.getStartMenu()));
    }

    public void sendChooseCategoryMenu(long id) throws TelegramApiException {
        this.executeAsync(MultiContentMessageSender.sendMessage(id,
                "Выберите категорию:",
                MarkupCreator.getMarkupForCategoryMessage()));
    }

    //TODO ПЕРЕДЕЛАТЬ ЛОГИКУ И ПЕРЕИМЕНОВАТЬ
    public void startCommandCheck(Update update) throws TelegramApiException {
        if (update.hasMessage() && update.getMessage().getText().equals("/start")) {
                sendStartMenu(update.getMessage().getChatId());
        } else if (update.hasMessage() && !update.getMessage().getText().equals("/start")) {

            var u = userStateService.findAllByChatId(update.getMessage().getChatId()).getLast();
            var user = userService.findUser(update.getMessage().getChatId());

            String state = u.getBotState();

            switch(state) {
                case ("NAME_REQUEST"):
                    user.setName(update.getMessage().getText());
                    userService.save(user);
                    userStateService.setState(update.getMessage().getChatId(), Optional.empty(), UpdateState.PHONE_REQUEST.name());
                    this.executeAsync(TextMessageSender.sendInfo(update.getMessage().getChatId(),
                            "Укажите свой номер телефона:"));
                    break;
                case ("PHONE_REQUEST"), ("UPDATE_PHONE"):
                    user.setNumberOfPhone(update.getMessage().getText());
                    u.setBotState(UpdateState.CHECK_STATE.name());
                    userService.save(user);
                    userStateService.save(u);
                    sendSuggestionToCheckData(user, update);
                    break;
                case ("UPDATE_NAME"):
                    user.setName(update.getMessage().getText());
                    u.setBotState(UpdateState.CHECK_STATE.name());
                    userService.save(user);
                    userStateService.save(u);
                    sendSuggestionToCheckData(user, update);
                    break;
            }
        }
    }

    public void sendSuggestionToCheckData(ShopUser user, Update update) throws TelegramApiException {
        String checkSuggest = "Пожалуйста, проверьте ваши данные:\n\n"
                + "Имя: " + user.getName() + "\n\n" + "Номер телефона: " + user.getNumberOfPhone();
        this.executeAsync(MultiContentMessageSender.sendMessage(user.getChatId(), checkSuggest,
                MarkupCreator.getMarkupForCheckDataOfUserMenu()));
     }

    public void sendRequestForAdmin(ShopUser user, Update update) throws TelegramApiException {
        String content = "Готово! Информация о вашем заказе передана администрации нашего магазина, ожидайте"
                + " обратной связи!";
        long chatId = update.getCallbackQuery().getFrom().getId();
        this.executeAsync(TextMessageSender.sendInfo(chatId, content));

        User userFromTg = update.getCallbackQuery().getFrom();

        String fullName;
        String userName;
        if (userFromTg.getLastName() != null) {
            fullName = userFromTg.getFirstName() + " " + userFromTg.getLastName();
        } else {
            fullName = "Нет информации";
        }
        if (userFromTg.getUserName() != null) {
            userName = "@" + userFromTg.getUserName();
        } else {
            userName = "Нет информации";
        }
        String contentForRequest = String.format("Новый заказ!\n\n Имя клиента:" +
                " %s\n\n ФИО: %s\n\n Ник в телеграм: %s\n\n Номер телефона: %s\n\n %s",
                user.getName(), fullName, userName, user.getNumberOfPhone(), user.getListOfRequests());

        this.execute(TextMessageSender.sendInfo(id_admin1, contentForRequest));
//        this.execute(TextMessageSender.sendInfo(id_admin2, contentForRequest));
        user.setListOfRequests("");
        userStateService.setState(chatId, Optional.empty(), UpdateState.SUCCESS_STATE.name());
        userService.save(user);
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
