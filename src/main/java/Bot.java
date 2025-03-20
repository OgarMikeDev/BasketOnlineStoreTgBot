import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bot extends TelegramLongPollingBot {
    //Map для хранения товаров каждого клиента
    private static HashMap<Long, Collagen> mapCollagen = new HashMap<>();
    //URL категории жидкого коллагена
    private String urlWebPageWithLiquidCategoryCollagen = "https://kollagen.life/product-category/pitevoj-kollagen";
    //Путь к файлу с html-кодом 
    private String pathToFileWithHtmlCode = "src/main/resources/data/htmlCodeWebPage.html";
    //Название текущего коллагена
    String currentNameCollagen = "";
    //Цена текущего коллагена
    int currentPriceCollagen = 0;

    //Кнопка для добавления товара в корзину
    private InlineKeyboardButton buttonForAddCollagenInBasket = InlineKeyboardButton.builder()
            .text("Добавить в корзину")
            .callbackData("добавить в корзину")
            .build();
    //Кнопка для возврата назад
    private InlineKeyboardButton buttonForReturnBack = InlineKeyboardButton.builder()
            .text("Вернуться на главную")
            .callbackData("вернуться на главную")
            .build();
    //Клавиатура для кнопки для добавления товара в корзину
    private InlineKeyboardMarkup keyboardForButtonForAddCollagenInBasket = InlineKeyboardMarkup.builder()
            .keyboardRow(List.of(buttonForAddCollagenInBasket))
            //.keyboardRow(List.of(buttonForReturnBack))
            .build();

    //Кнопка для запуска тг-бота
    private InlineKeyboardButton buttonForStartTgBot = InlineKeyboardButton.builder()
            .text("Запуск")
            .callbackData("запуск")
            .build();
    //Клавиатура для кнопки для запуска тг-бота
    private InlineKeyboardMarkup keyboardForButtonForStartTgBot = InlineKeyboardMarkup.builder()
            .keyboardRow(List.of(buttonForStartTgBot))
            .build();

    //Кнопка для вывода всех товаров
    private InlineKeyboardButton buttonForGetListItems = InlineKeyboardButton.builder()
            .text("Посмотреть все товары")
            .callbackData("посмотреть все товары")
            .build();
    //Кнопка для вывода всех товаров
    private InlineKeyboardButton buttonForMyBasket = InlineKeyboardButton.builder()
            .text("Моя корзина")
            .callbackData("моя корзина")
            .build();
    //Клавиатура для кнопки для вывода всех товаров
    private InlineKeyboardMarkup keyboardForButtonForGetListItems = InlineKeyboardMarkup.builder()
            .keyboardRow(List.of(buttonForGetListItems))
            .keyboardRow(List.of(buttonForMyBasket))
            .build();

    //Кнопка для категории жидкого коллагена
    private InlineKeyboardButton buttonForLiquidCollagen = InlineKeyboardButton.builder()
            .text("Питьевой жидкий коллаген")
            .callbackData("жидкий коллаген")
            .build();
    //Кнопка для категории коллагена в порошке
    private InlineKeyboardButton buttonForPowderCollagen = InlineKeyboardButton.builder()
            .text("Коллаген в порошке")
            .callbackData("коллаген в порошке")
            .build();
    //Кнопка для категории таблетированного коллагена
    private InlineKeyboardButton buttonForTabletCollagen = InlineKeyboardButton.builder()
            .text("Коллаген в таблетках")
            .callbackData("коллаген в таблетках")
            .build();
    //Клавиатура для кнопки для вывода всех категорий товаров
    private InlineKeyboardMarkup keyboardForButtonForAllCategories = InlineKeyboardMarkup.builder()
            .keyboardRow(List.of(buttonForLiquidCollagen))
            .keyboardRow(List.of(buttonForPowderCollagen))
            .keyboardRow(List.of(buttonForTabletCollagen))
            .keyboardRow(List.of(buttonForReturnBack))
            .build();

    //Кнопка для коллагена DHC 12000mg
    private InlineKeyboardButton buttonForСollagenDHC12000 = InlineKeyboardButton.builder()
            .text("DHC коллаген 12000mg питьевой 50 мл x 10")
            .callbackData("жидкий коллаген DHC 12000mg")
            .build();
    //Кнопка для коллагена DHC 12000mg
    private InlineKeyboardButton buttonForСollagenShiseidoRelacle = InlineKeyboardButton.builder()
            .text("Collagen Shiseido Relacle желе")
            .callbackData("collagen Shiseido Relacle желе")
            .build();
    //Клавиатура для кнопки для коллагена DHC 12000mg
    private InlineKeyboardMarkup keyboardForButtonForLiquidCollagen = InlineKeyboardMarkup.builder()
            .keyboardRow(List.of(buttonForСollagenDHC12000))
            .keyboardRow(List.of(buttonForСollagenShiseidoRelacle))
            .keyboardRow(List.of(buttonForReturnBack))
            .build();


    @Override
    public void onUpdateReceived(Update update) {
        forWorkWithText(update);
        forWorkWithButtons(update);
    }

    public void forWorkWithText(Update update) {
        if (update.hasMessage()) {
            String userId = update.getMessage().getFrom().getId().toString();
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(userId)
                    .text("")
                    .build();

            String text = update.getMessage().getText();
            if (text.equals("/start")) {
                sendMessage.setText(
                        "Добро пожаловать!\n" +
                                "Вас приветствует телеграмм-бот\n" +
                                "нашего интернет-магазина :)");
                sendMessage.setReplyMarkup(keyboardForButtonForStartTgBot);
            }

            try {
                execute(sendMessage);
            } catch (Exception ex) {
                ex.getMessage();
            }
        }
    }

    public void forWorkWithButtons(Update update) {
        if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            Integer messageId = update.getCallbackQuery().getMessage().getMessageId();

            //Для отправки текста
            EditMessageText editMessageText = EditMessageText.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .text("")
                    .build();
            //Для отправки клавиатур с кнопками
            EditMessageReplyMarkup editMessageReplyMarkup = EditMessageReplyMarkup.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .build();
            //Для отправки фотографий
            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(chatId);

            if (callbackData.equals(buttonForStartTgBot.getCallbackData()) ||
                    callbackData.equals(buttonForReturnBack.getCallbackData())) {
                editMessageText.setText("Выберите одну из команд:");
                editMessageReplyMarkup.setReplyMarkup(keyboardForButtonForGetListItems);
            } else if (callbackData.equals(buttonForGetListItems.getCallbackData())) {
                editMessageText.setText("Выберите категорию товаров");
                editMessageReplyMarkup.setReplyMarkup(keyboardForButtonForAllCategories);
            } else if (callbackData.equals(buttonForLiquidCollagen.getCallbackData())) {
                editMessageText.setText("Питьевой коллаген");
                editMessageReplyMarkup.setReplyMarkup(keyboardForButtonForLiquidCollagen);
            } else if (callbackData.equals(buttonForСollagenDHC12000.getCallbackData())) {
                currentPriceCollagen = forGetPriceCollagenWithSelectedCategory(buttonForСollagenDHC12000.getText(), urlWebPageWithLiquidCategoryCollagen);
                currentNameCollagen = buttonForСollagenDHC12000.getText();
                Collagen currentCollagen = new Collagen(currentNameCollagen, currentPriceCollagen);
                mapCollagen.put(chatId, currentCollagen);
                sendPhoto.setCaption(currentNameCollagen + " за " + currentPriceCollagen + " руб.");
                sendPhoto.setPhoto(new InputFile(new File("src/main/resources/data/dhc12000.jpg")));
                sendPhoto.setReplyMarkup(keyboardForButtonForAddCollagenInBasket);
            } else if (callbackData.equals(buttonForСollagenShiseidoRelacle.getCallbackData())) {
                currentPriceCollagen = forGetPriceCollagenWithSelectedCategory(buttonForСollagenShiseidoRelacle.getText(), urlWebPageWithLiquidCategoryCollagen);
                currentNameCollagen = buttonForСollagenShiseidoRelacle.getText();
                Collagen currentCollagen = new Collagen(currentNameCollagen, currentPriceCollagen);
                mapCollagen.put(chatId, currentCollagen);
                sendPhoto.setCaption(currentNameCollagen + " за " + currentPriceCollagen + " руб.");
                sendPhoto.setPhoto(new InputFile(new File("src/main/resources/data/heseido_relacle_collagen_pitevoy.jpg")));
                sendPhoto.setReplyMarkup(keyboardForButtonForAddCollagenInBasket);
            } else if (callbackData.equals(buttonForMyBasket.getCallbackData())) {
                for (Map.Entry<Long, Collagen> allCollagen : mapCollagen.entrySet()) {
                    if (allCollagen.getKey().equals(chatId)) {
                        editMessageText.setText("Вывод всех товаров: " + allCollagen.getValue());
                    }
                }
            }

            String strSendPhoto = String.valueOf(sendPhoto);
            System.out.println("Send photo: " + strSendPhoto);
            int leftIndexForCaption = strSendPhoto.indexOf("caption=") + "caption=".length();
            int rightIndexForCaption = strSendPhoto.indexOf(",", leftIndexForCaption);
            String caption = strSendPhoto.substring(leftIndexForCaption, rightIndexForCaption);
            String availablePhoto = caption.equals("null") ? "нет" : "да";
            System.out.println("Наличие фотографии: " + availablePhoto);
            if (callbackData.equals(buttonForAddCollagenInBasket.getCallbackData())) {
                Collagen currentCollagen = new Collagen(currentNameCollagen, currentPriceCollagen);
            }
            try {
                if (!caption.equals("null")) {
                    execute(sendPhoto);
                    System.out.println("Отправка фотки");
                }

                execute(editMessageText);
                System.out.println("Отправка текста");

                execute(editMessageReplyMarkup);
                System.out.println("Отправка клавы");

                System.out.println("Все отправления прошли!\n");
            } catch (Exception ex) {
                ex.getMessage();
            }
        }
    }

    public int forGetPriceCollagenWithSelectedCategory(String captionCollagen, String urlWebPageWithCategoryCollagen) {
        int priceCollagen = 0;
        try {
            Document document = Jsoup.connect(urlWebPageWithLiquidCategoryCollagen).get();
            String strHtmlCode = String.valueOf(document);

            Elements elements = document.select(".jet-woo-products__inner-box");
            for (Element currentElement : elements) {
                String strCurrentElement = String.valueOf(currentElement).strip();
                String templateForPrice = captionCollagen + "</a></h2>\n" +
                        " <div class=\"jet-woo-product-price\">\n" +
                        "  <span class=\"woocommerce-Price-amount amount\"><bdi>";
                int leftIndexForPrice = strCurrentElement.indexOf(templateForPrice);
                if (leftIndexForPrice != -1) {
                    leftIndexForPrice += templateForPrice.length();
                    int rightIndexForPrice = strCurrentElement.indexOf("&", leftIndexForPrice);
                    String strPrice = strCurrentElement.substring(leftIndexForPrice, rightIndexForPrice).replace(",", "");
                    priceCollagen = Integer.parseInt(strPrice);
                }
            }

            FileWriter fileWriter = new FileWriter(pathToFileWithHtmlCode);
            fileWriter.write(strHtmlCode);
        } catch (Exception ex) {
            ex.getMessage();
        }
        System.out.println("Цена коллагена - " + priceCollagen);
        return priceCollagen;
    }

    @Override
    public String getBotUsername() {
        return "@basket_online_store_tg_bot";
    }

    @Override
    public String getBotToken() {
        return "7785069816:AAGsxaM_rYQLCC3mW-j-QIj5qBrIM576GRQ";
    }
}