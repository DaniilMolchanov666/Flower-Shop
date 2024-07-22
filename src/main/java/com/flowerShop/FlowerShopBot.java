package com.flowerShop;

import com.flowerShop.model.Product;
import com.flowerShop.model.ShopUser;
import com.flowerShop.model.UserState;
import com.flowerShop.sender.DeleteMessageUpdate;
import com.flowerShop.sender.MultiContentMessageSender;
import com.flowerShop.sender.PhotoSender;
import com.flowerShop.sender.TextMessageSender;
import com.flowerShop.service.bot.user_service.UserServiceImpl;
import com.flowerShop.service.bot.user_state_service.UserStateService;
import com.flowerShop.service.web.ProductsServiceImpl;
import com.flowerShop.util.bot.UpdateState;
import com.flowerShop.util.bot.markups.AllRequestsMenuMarkup;
import com.flowerShop.util.bot.markups.BackMenuButtonMarkup;
import com.flowerShop.util.bot.markups.CheckMenuMarkup;
import com.flowerShop.util.bot.markups.CategoryMenuMarkup;
import com.flowerShop.util.bot.markups.BackStartButtonMarkup;
import com.flowerShop.util.bot.markups.DeleteMenuMarkup;
import com.flowerShop.util.bot.markups.PhotoMenuMarkup;
import com.flowerShop.util.bot.markups.StartMenuMarkup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;
import java.util.List;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Arrays;
import java.util.stream.Collectors;

//TODO добавить возможность скрывать букеты от пользователей бота
@Slf4j
@Service
public class FlowerShopBot extends TelegramLongPollingBot {

    private final String botUserName;

    @Autowired
    private ProductsServiceImpl productsService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserStateService userStateService;

    private final List<Long> listOfIdByAdminsForBringRequests = List.of(1402556700L, 176367978L, 6831132148L);

    public FlowerShopBot(@Value("${telegram.bot.token}") String botToken,
                         @Value("{telegram.bot.name") String botUserName) {
        super(botToken);
        this.botUserName = botUserName;
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
//            setInfoAboutUserForConnect(update);
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
            case "Цветы", "Монобукет", "Составной букет", "Композиция":
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
                getBucket(update, shopUser, lastProduct);
                break;
            case "BACK_TO_BUCKET_BUTTON", "BUCKET_BUTTON":
                getBucket(update, shopUser, Optional.empty());
                break;
            case "LETTER_BUTTON":
                this.executeAsync(DeleteMessageUpdate.delete(update));
                String letterMessage = "Мы считаем, что добрые слова греют душу не меньше цветов, "
                        + "именно поэтому мы дарим открытку к любому заказу.\n"
                        + "Текст теплого послания можно оставить ниже";
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
                    this.executeAsync(DeleteMessageUpdate.delete(update));
                    sendSuggestionToCheckData(shopUser, update);
                } else {
                    userStateService.setState(id, Optional.empty(), UpdateState.NAME_REQUEST.name());
                    this.executeAsync(TextMessageSender.sendInfo(id, "Укажите как к вам обращаться:"));
                    this.executeAsync(DeleteMessageUpdate.delete(update));
                }
                break;
            case "UPDATE_NAME_BUTTON":
                userStateService.setState(id, Optional.empty(), UpdateState.UPDATE_NAME.name());
                this.executeAsync(TextMessageSender.sendInfo(id, "Укажите как к вам обращаться:"));
                this.executeAsync(DeleteMessageUpdate.delete(update));
                break;
            case "UPDATE_PHONE_BUTTON":
                userStateService.setState(id, Optional.empty(), UpdateState.UPDATE_PHONE.name());
                this.executeAsync(TextMessageSender.sendInfo(id, "Укажите свой номер телефона:"));
                this.executeAsync(DeleteMessageUpdate.delete(update));
                break;
            case "SEND_REQUEST_BUTTON":
                userStateService.setState(id, Optional.empty(), UpdateState.SUCCESS_STATE.name());
                sendRequestForAdmin(shopUser, update);
                break;
            case "HELP_BUTTON":
                String helpMessage = "Если у Вас возник вопрос или "
                        + "Вы хотите уточнить/дополнить информацию по Вашему заказу, "
                        + "то @Procvetanie_Shop с удовольствием Вам помогут";
                this.executeAsync(MultiContentMessageSender.sendMessage(update, helpMessage,
                        new BackStartButtonMarkup().createMarkup()));
                this.executeAsync(DeleteMessageUpdate.delete(update));
                break;
            default:
                StringBuilder message = new StringBuilder();
                if (!currentButton.contains("DELETE_BUTTON")) {
                    Optional<Product> currentProduct = productsService.findById(Integer.parseInt(currentButton));
                    if (currentProduct.isPresent()) {
                        this.executeAsync(PhotoSender.sendMessage(update, currentProduct.get(),
                                new DeleteMenuMarkup(currentProduct).createMarkup()));
                        this.executeAsync(DeleteMessageUpdate.delete(update));
                    } else {
                        getBucket(update, shopUser, Optional.empty());
                    }
                } else {
                    String pressedProductName = StringUtils.removeStart(currentButton, "DELETE_BUTTON");
                    Optional<Product> currentProduct = productsService.findById(Integer.parseInt(pressedProductName));

                    List<Product> listOfProductsExcludeDeleted =
                            new LinkedList<>(Arrays.stream(shopUser.getListOfRequests()
                                            .split("\n"))
                                    .map(i -> productsService.findProduct(i).orElse(null))
                                    .toList());

                    listOfProductsExcludeDeleted.remove(currentProduct.orElse(null));
                    listOfProductsExcludeDeleted.removeIf(Objects::isNull);

                    if (currentProduct.isEmpty()) {
                        message.append("(некоторый контент был удален администрацией)\n");
                    }

                    shopUser.setListOfRequests(listOfProductsExcludeDeleted.stream()
                            .map(Product::getName)
                            .collect(Collectors.joining("\n")));
                    userService.save(shopUser);

                    if (listOfProductsExcludeDeleted.isEmpty()) {
                        message.append("Ваша корзина пуста!");
                        this.executeAsync(MultiContentMessageSender.sendMessage(update, message.toString(),
                                new BackMenuButtonMarkup().createMarkup()));
                    } else {
                        message.append("Ваша корзина:\n");
                        this.executeAsync(MultiContentMessageSender
                                .sendMessage(update, message.toString(),
                                        new AllRequestsMenuMarkup(listOfProductsExcludeDeleted).createMarkup()));
                    }
                    this.executeAsync(DeleteMessageUpdate.delete(update));
                }
                break;
        }
    }

    private void getBucket(Update update, ShopUser userOfBot, Optional<Product> lastProduct) throws TelegramApiException {

        String emptyStringForFirstRequest = "";
        StringBuilder message = new StringBuilder();
        String requests = userOfBot.getListOfRequests() != null ? userOfBot.getListOfRequests() : emptyStringForFirstRequest;

        List<Product> listOfRequests = new LinkedList<>(Arrays.stream(requests.split("\n"))
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
                .collect(Collectors.joining("\n")));
        userService.save(userOfBot);

        if (listOfRequests.isEmpty()) {
            this.executeAsync(MultiContentMessageSender.sendMessage(update, message.append("Ваша корзина пуста!").toString(),
                    new BackMenuButtonMarkup().createMarkup()));
            this.executeAsync(DeleteMessageUpdate.delete(update));
        } else {
            this.executeAsync(MultiContentMessageSender
                    .sendMessage(update, message.append("Ваша корзина:\n").toString(),
                            new AllRequestsMenuMarkup(listOfRequests).createMarkup()));
            this.executeAsync(DeleteMessageUpdate.delete(update));
        }
    }

    public void setStateForUserAndSendProduct(Update update, Optional<Product> product, String state) throws TelegramApiException {
        long id = update.getCallbackQuery().getFrom().getId();
        if (product.isPresent()) {
            var list = productsService.findByCategory(product.get().getCategory());
            var photoSender = PhotoSender.sendMessage(update, product.get(),
                    new PhotoMenuMarkup().createMarkup());

            String numberOfProduct = String.format("""
                            %s
                            
                            ( %d из %d )
                            
                            %s""",
                    photoSender.getCaption(),
                    list.indexOf(product.get()) + 1,
                    list.size(),
                    product.get().getDescription());

            photoSender.setCaption(numberOfProduct);

            this.executeAsync(photoSender);
            this.executeAsync(DeleteMessageUpdate.delete(update));

            userStateService.setState(id, product, state);
        } else {
            this.executeAsync(MultiContentMessageSender.sendMessage(update, "К сожалению, товар раскупили. " +
                            "Узнать о ближайшем поступлении можно в чате с флористом @Procvetanie_Shop",
                    new BackMenuButtonMarkup().createMarkup()));
            this.executeAsync(DeleteMessageUpdate.delete(update));
        }
    }

    public void sendStartMenu(Update update) throws TelegramApiException {
        String greeting = "Добро пожаловать! Что вас интересует?";
        this.executeAsync(MultiContentMessageSender.sendMessage(update, greeting, new StartMenuMarkup().createMarkup()));
        this.executeAsync(DeleteMessageUpdate.delete(update));
    }

    public void sendChooseCategoryMenu(Update update) throws TelegramApiException {
        this.executeAsync(MultiContentMessageSender.sendMessage(update, "Выберите категорию:",
                new CategoryMenuMarkup().createMarkup()));
        this.executeAsync(DeleteMessageUpdate.delete(update));
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
                    getBucket(update, user, Optional.empty());
                    break;
            }
        }
    }

    public void sendSuggestionToCheckData(ShopUser user, Update update) throws TelegramApiException {

        String postcardText = user.getPostcardText() == null ? "Без текста" : user.getPostcardText();

        String checkSuggest = String.format("""
                        Пожалуйста, проверьте Ваши данные:
                        
                        Имя: %s
                        Номер телефона: %s
                        
                        Текст открытки: %s
                        
                        Ваш заказ:
                        
                        %s
                        """,
                user.getName(),
                user.getNumberOfPhone(),
                postcardText,
                getRequestsText(user));

        this.executeAsync(MultiContentMessageSender.sendMessage(update, checkSuggest,
                new CheckMenuMarkup().createMarkup()));
    }

    //TODO сделать сохранение информации о пользователе на старте работы с ботом
    public void setInfoAboutUserForConnect(Update update) {
        long chatId = update.getCallbackQuery().getFrom().getId();

        User userFromTg = update.getCallbackQuery().getFrom();

        String userName = userFromTg.getUserName() != null ? "@" + userFromTg.getUserName() : "Нет информации";

        ShopUser shopUser = ShopUser.builder().chatId(chatId).name(userName).build();

        userService.save(shopUser);

    }

    public void sendRequestForAdmin(ShopUser user, Update update) throws TelegramApiException {
        String content = """
                Готово! Информация о Вашем заказе передана флористам студии. \
                В ближайшее время с Вами свяжется администратор магазина для подтверждения заказа и уточнения деталей.
                
                Пожалуйста, ожидайте обратной связи\uD83E\uDEF6\uD83C\uDFFB \
                Благодарим Вас за доверие к нашему салону❤️""";

        long chatId = update.getCallbackQuery().getFrom().getId();
        this.executeAsync(TextMessageSender.sendInfo(chatId, content));

        User userFromTg = update.getCallbackQuery().getFrom();

        String fullName = userFromTg.getLastName() != null ? userFromTg.getFirstName() + " " + userFromTg.getLastName()
                : userFromTg.getFirstName();
        String userName = userFromTg.getUserName() != null ? "@" + userFromTg.getUserName() : "Нет информации";

        String contentForRequest = String.format("""
                        Новый заказ!
                        
                        Имя клиента: %s
                        ФИО: %s
                        Ник в телеграм: %s
                        Номер телефона: %s
                        
                        Текст открытки: %s
                        
                        Заказ:
                        
                        %s
                        """,
                user.getName(),
                fullName,
                userName,
                user.getNumberOfPhone(),
                user.getPostcardText() == null ? "Без текста" : user.getPostcardText(),
                getRequestsText(user));

        listOfIdByAdminsForBringRequests.forEach(i -> {
            try {
                this.executeAsync(TextMessageSender.sendInfo(i, contentForRequest));
            } catch (TelegramApiException e) {
                log.error("Ошибка с отправлением заказа администратору с id = {}", i);
            }
        });

        user.setListOfRequests("");

        userStateService.setState(chatId, Optional.empty(), UpdateState.SUCCESS_STATE.name());
        userService.save(user);
    }

    public String getRequestsText(ShopUser user) {
        List<Product> productList = Arrays.stream(user.getListOfRequests().split("\n"))
                .map(i -> productsService.findProduct(i).orElse(null))
                .filter(Objects::nonNull)
                .toList();

        String listOfRequests = productList.stream()
                .map(i -> "%s - %s р.".formatted(i.getName(), i.getPrice()))
                .collect(Collectors.joining("\n"));

        String sumOfRequestsPrices = productList.stream()
                .mapToInt(i -> Integer.parseInt(i.getPrice()))
                .sum() + "р.";

        return String.format("""
                        %s
                        
                        Общая сумма (без учета стоимости доставки): %s
                        
                        """,
                listOfRequests, sumOfRequestsPrices);
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }
}
