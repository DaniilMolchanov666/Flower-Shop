package com.flowerShop.Flower_Shop;

import com.flowerShop.Flower_Shop.config.FlowerShopBotConfig;
import com.flowerShop.Flower_Shop.model.Product;
import com.flowerShop.Flower_Shop.model.ShopUser;
import com.flowerShop.Flower_Shop.model.UserState;
import com.flowerShop.Flower_Shop.sender.MultiContentMessageSender;
import com.flowerShop.Flower_Shop.sender.PhotoSender;
import com.flowerShop.Flower_Shop.sender.TextMessageSender;
import com.flowerShop.Flower_Shop.service.bot.user_service.UserServiceImpl;
import com.flowerShop.Flower_Shop.service.bot.user_state_service.UserStateService;
import com.flowerShop.Flower_Shop.service.web.ProductsServiceImpl;
import com.flowerShop.Flower_Shop.util.bot.FlowerShopBotCommands;
import com.flowerShop.Flower_Shop.util.bot.MarkupCreator;
import com.flowerShop.Flower_Shop.util.bot.UpdateState;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@SpringBootApplication
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
    public FlowerShopBot(FlowerShopBotConfig config) {

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
            checkTextMessagesFromUser(update);
        } else if (update.hasCallbackQuery()) {
            buttonsCheck(update);
        }
    }

    public void buttonsCheck(Update update) throws TelegramApiException {
        String currentButton = update.getCallbackQuery().getData();
        long id = update.getCallbackQuery().getFrom().getId();
        var shopUser = userService.findUser(id);
        UserState userState;
        Optional<Product> lasViewedProductOfUser = userStateService.getLasViewedProductOfUser(id);
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
                sendChooseCategoryMenu(update);
                break;
            case "BACK_START_BUTTON", "BACK_TO_START_BUTTON":
                sendStartMenu(update);
                break;
            case "FORWARD_BUTTON", "BACKWARD_BUTTON":
                if (lasViewedProductOfUser.isPresent()) {
                    Product product = lasViewedProductOfUser.get();
                    List<Product> list = productsService.findByCategory(product.getCategory());
                    if (currentButton.equals("FORWARD_BUTTON")) {
                        index = list.indexOf(product) >= list.size() - 1
                                ? list.indexOf(product) : list.indexOf(product) + 1;
                    } else {
                        index = list.indexOf(product) <= 0
                                ? list.indexOf(product) : list.indexOf(product) - 1;
                    }
                    setStateForUserAndSendProduct(update, Optional.of(list.get(index)), UpdateState.FLOWERS.name());
                } else {
                    setStateForUserAndSendProduct(update, Optional.empty(), UpdateState.FLOWERS.name());
                }
                break;
            case "REQUEST_BUTTON", "DELETE_BUTTON":
                Optional<Product> lastProduct = userStateService.getLasViewedProductOfUser(id);

                if (currentButton.equals("DELETE_BUTTON")) {
                    shopUser = userService.findUser(id);
                    shopUser.setListOfRequests(null);
                    userService.save(shopUser);
                    sendChooseCategoryMenu(update);
                    return;
                }
                getBucket(update, shopUser, lastProduct, id);
                break;
            case "BACK_TO_BUCKET_BUTTON", "BUCKET_BUTTON":
                getBucket(update, shopUser, Optional.empty(), id);
                break;
            case "LETTER_BUTTON":
                this.executeAsync(TextMessageSender.deleteMessage(update));
                String letterMessage = "Мы считаем, что добрые слова греют душу не меньше цветов, именно поэтому мы дарим открытку к любому заказу.\n" +
                        "Текст теплого послания можно оставить ниже";
                userStateService.setState(id, lasViewedProductOfUser, UpdateState.LETTER_STATE.name());
                this.executeAsync(TextMessageSender.sendInfo(id, letterMessage));
                break;
            case "CONTINUE_BUTTON":
                userState = userStateService.findByChatId(id);
                var product1 = productsService
                        .findById(Integer.parseInt(userState.getLastViewedProduct()))
                        .orElse(null);
                setStateForUserAndSendProduct(update, Optional.ofNullable(product1), UpdateState.FLOWERS.name());
                break;
            case "END_REQUEST_BUTTON":
                if (shopUser.getName() != null && shopUser.getNumberOfPhone() != null) {
                    this.executeAsync(TextMessageSender.deleteMessage(update));
                    sendSuggestionToCheckData(shopUser, update);
                } else {
                    userStateService.setState(id, Optional.empty(), UpdateState.NAME_REQUEST.name());
                    this.executeAsync(TextMessageSender.sendInfo(id, "Укажите как к вам обращаться:"));
                    this.executeAsync(PhotoSender.deleteMessage(update));
                }
                break;
            case "UPDATE_NAME_BUTTON":
                userStateService.setState(id, Optional.empty(), UpdateState.UPDATE_NAME.name());
                this.executeAsync(TextMessageSender.sendInfo(id, "Укажите как к вам обращаться:"));
                this.executeAsync(TextMessageSender.deleteMessage(update));
                break;
            case "UPDATE_PHONE_BUTTON":
                userStateService.setState(id, Optional.empty(), UpdateState.UPDATE_PHONE.name());
                this.executeAsync(TextMessageSender.sendInfo(id, "Укажите свой номер телефона:"));
                this.executeAsync(TextMessageSender.deleteMessage(update));
                break;
            case "SEND_REQUEST_BUTTON":
                userStateService.setState(id, Optional.empty(), UpdateState.SUCCESS_STATE.name());
                sendRequestForAdmin(shopUser, update);
                break;
            case "HELP_BUTTON":
                String helpMessage = "Если у Вас возник вопрос или Вы хотите уточнить/дополнить информацию по Вашему заказу, " +
                        "то @Procvetanie_Shop с удовольствием Вам помогут";
                this.executeAsync(MultiContentMessageSender.sendMessage(update, helpMessage, MarkupCreator.getBackButton()));
                this.executeAsync(MultiContentMessageSender.deleteMessage(update));
                break;
            default:
                StringBuilder message = new StringBuilder();
                if (!currentButton.contains("DELETE_BUTTON")) {
                    Optional<Product> currentProduct = productsService.findProduct(currentButton);
                    if (currentProduct.isPresent()) {
                        this.executeAsync(PhotoSender.sendMessage(update, currentProduct.get(),
                                MarkupCreator.getDeleteProductMenu(currentProduct)));
                        this.executeAsync(PhotoSender.deleteMessage(update));
                    } else {
                        getBucket(update, shopUser, Optional.empty(), id);
                    }
                } else {
                    String pressedProductName = StringUtils.removeStart(currentButton, "DELETE_BUTTON");
                    Optional<Product> currentProduct = productsService.findProduct(pressedProductName);

                    List<Product> listOfProductsExcludeDeleted = new LinkedList<>(Arrays.stream(shopUser.getListOfRequests()
                                    .split(", "))
                            .map(i -> productsService.findProduct(i).orElse(null))
                            .toList());

                    listOfProductsExcludeDeleted.remove(currentProduct.orElse(null));
                    listOfProductsExcludeDeleted.removeIf(Objects::isNull);

                    if (currentProduct.isEmpty()) {
                        message.append("(некоторый контент был удален администрацией)\n");
                    }

                    shopUser.setListOfRequests(listOfProductsExcludeDeleted.stream()
                            .map(Product::getName)
                            .collect(Collectors.joining(", ")));
                    userService.save(shopUser);

                    if (listOfProductsExcludeDeleted.isEmpty()) {
                        message.append("Ваша корзина пуста!");
                        this.executeAsync(MultiContentMessageSender.sendMessage(update, message.toString(), MarkupCreator.getBackMenuButton()));
                        this.executeAsync(MultiContentMessageSender.deleteMessage(update));
                    } else {
                        message.append("Ваша корзина:\n");
                        this.executeAsync(MultiContentMessageSender
                                .sendMessage(update, message.toString(),
                                        MarkupCreator.getButtonsForALlRequestsInBucket(listOfProductsExcludeDeleted)));
                        this.executeAsync(MultiContentMessageSender.deleteMessage(update));
                    }
                }
                break;
        }
    }

    private void getBucket(Update update, ShopUser userOfBot, Optional<Product> lastProduct, long id) throws TelegramApiException {

        String emptyStringForFirstRequest = "";
        StringBuilder message = new StringBuilder();
        String requests = userOfBot.getListOfRequests() != null ? userOfBot.getListOfRequests() : emptyStringForFirstRequest;

        List<Product> listOfRequests = new LinkedList<>(Arrays.stream(requests.split(", "))
                .dropWhile(String::isEmpty)
                .map(i -> productsService.findProduct(i).orElse(null))
                .toList());

        if (listOfRequests.contains(null)) {
            message.append("(некоторый контент был удален администрацией)\n");
        }

        listOfRequests.add(lastProduct.orElse(null));
        listOfRequests.removeIf(Objects::isNull);

        userOfBot.setListOfRequests(listOfRequests.stream()
                .map(Product::getName)
                .collect(Collectors.joining(", ")));
        userService.save(userOfBot);

        if (listOfRequests.isEmpty()) {
            this.executeAsync(MultiContentMessageSender.sendMessage(update, message.append("Ваша корзина пуста!").toString(),
                    MarkupCreator.getBackMenuButton()));
            this.executeAsync(MultiContentMessageSender.deleteMessage(update));
        } else {
            this.executeAsync(MultiContentMessageSender
                    .sendMessage(update, message.append("Ваша корзина:\n").toString(),
                            MarkupCreator.getButtonsForALlRequestsInBucket(listOfRequests)));
            this.executeAsync(MultiContentMessageSender.deleteMessage(update));
        }
    }

    public void setStateForUserAndSendProduct(Update update, Optional<Product> product, String state) throws TelegramApiException {
        long id = update.getCallbackQuery().getFrom().getId();
        if (product.isPresent()) {
            var list = productsService.findByCategory(product.get().getCategory());
            var photoSender = PhotoSender.sendMessage(update, product.get(), MarkupCreator.getMarkupForMainMenu());

            String numberOfProduct = "%s\n\n( %d из %d )\n\n%s".formatted(photoSender.getCaption(),
                    list.indexOf(product.get()) + 1, list.size(),
                    product.get().getDescription());

            photoSender.setCaption(numberOfProduct);

            this.executeAsync(photoSender);
            this.executeAsync(PhotoSender.deleteMessage(update));

            userStateService.setState(id, product, state);
        } else {
            this.executeAsync(MultiContentMessageSender.sendMessage(update, "К сожалению, товар раскупили. " +
                            "Узнать о ближайшем поступлении можно в чате с флористом @Procvetanie_Shop",
                    MarkupCreator.getBackMenuButton()));
            this.executeAsync(PhotoSender.deleteMessage(update));
        }
    }

    public void sendStartMenu(Update update) throws TelegramApiException {
        String greeting = "Добро пожаловать! Что вас интересует?";
        this.executeAsync(MultiContentMessageSender.sendMessage(update, greeting, MarkupCreator.getStartMenu()));
        this.executeAsync(MultiContentMessageSender.deleteMessage(update));
    }

    public void sendChooseCategoryMenu(Update update) throws TelegramApiException {
        this.executeAsync(MultiContentMessageSender.sendMessage(update, "Выберите категорию:",
                MarkupCreator.getMarkupForCategoryMessage()));
        this.executeAsync(MultiContentMessageSender.deleteMessage(update));
    }

    public void checkTextMessagesFromUser(Update update) throws TelegramApiException {
        if (update.hasMessage() && update.getMessage().getText().equals("/start")) {
            sendStartMenu(update);
        } else if (update.hasMessage() && !update.getMessage().getText().equals("/start")) {
            var u = userStateService.findByChatId(update.getMessage().getChatId());
            var user = userService.findUser(update.getMessage().getChatId());

            String state = u.getBotState();

            switch (state) {
                case ("NAME_REQUEST"):
                    user.setName(update.getMessage().getText());
                    userService.save(user);
                    userStateService.setState(update.getMessage().getChatId(), Optional.empty(),
                            UpdateState.PHONE_REQUEST.name());
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
                case ("LETTER_STATE"):
                    user.setPostcardText(update.getMessage().getText());
                    userService.save(user);
                    getBucket(update, user, Optional.empty(), update.getMessage().getChatId());
                    break;
            }
        }
    }

    public void sendSuggestionToCheckData(ShopUser user, Update update) throws TelegramApiException {
        List<Optional<Product>> productList = Arrays.stream(user.getListOfRequests().split(", "))
                .map(i -> productsService.findProduct(i))
                .collect(Collectors.toList());
        productList.removeIf(Optional::isEmpty);
        String checkSuggest = "Пожалуйста, проверьте ваши данные:\n\n" +
                "Имя: " + user.getName() + "\n\n" + "Номер телефона: " + user.getNumberOfPhone() + "\n"
                + "\nТекст открытки: "
                + (Objects.isNull(user.getPostcardText()) ? "Без текста" : user.getPostcardText())
                + "\n\nВаш заказ:\n"
                + productList.stream()
                .map(i -> i.map(product -> String.format("%s - %s р.",
                                product.getName(), product.getPrice()))
                        .orElse(""))
                .collect(Collectors.joining("\n"))
                + "\n\nОбщая сумма:\n"
                + productList.stream().mapToInt(i -> i.map(product -> Integer.parseInt(product.getPrice())).orElse(0)).sum() + "р.";
        this.executeAsync(MultiContentMessageSender.sendMessage(update, checkSuggest,
                MarkupCreator.getMarkupForCheckDataOfUserMenu()));
    }

    public void sendRequestForAdmin(ShopUser user, Update update) throws TelegramApiException {
        String content = """
                Готово! Информация о Вашем заказе передана флористам студии. \
                В ближайшее время с Вами свяжется администратор магазина для подтверждения заказа и уточнения деталей.

                Пожалуйста, ожидайте обратной связи\uD83E\uDEF6\uD83C\uDFFB \
                Благодарим Вас за доверие к нашему салону❤\uFE0F""";
        long chatId = update.getCallbackQuery().getFrom().getId();
        this.executeAsync(TextMessageSender.sendInfo(chatId, content));

        User userFromTg = update.getCallbackQuery().getFrom();

        String fullName = userFromTg.getLastName() != null ? userFromTg.getFirstName() + " " + userFromTg.getLastName() :
                userFromTg.getFirstName();
        String userName = userFromTg.getUserName() != null ? "@" + userFromTg.getUserName() : "Нет информации";

        String contentForRequest = String.format("""
                        Новый заказ!

                        Имя клиента:\
                        %s

                        ФИО: %s

                        Ник в телеграм: %s

                        Номер телефона: %s
                                                
                        Текст открытки: %s

                        %s""",
                user.getName(),
                fullName,
                userName,
                user.getNumberOfPhone(),
                Objects.isNull(user.getPostcardText()) ? "Без текста" : user.getPostcardText(),
                Optional.of(user.getListOfRequests()).orElse("Без заказа"));

        this.execute(TextMessageSender.sendInfo(id_admin1, contentForRequest));
        this.execute(TextMessageSender.sendInfo(id_admin2, contentForRequest));

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
