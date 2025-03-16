import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Bot extends TelegramLongPollingBot {
    //Map для хранения товаров каждого клиента
    //private HashMap<Long, >

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
            .keyboardRow(List.of(buttonForAddCollagenInBasket, buttonForReturnBack))
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
            .keyboardRow(List.of(buttonForReturnBack))
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
    //Клавиатура для кнопки для коллагена DHC 12000mg
    private InlineKeyboardMarkup keyboardForButtonForLiquidCollagen = InlineKeyboardMarkup.builder()
            .keyboardRow(List.of(buttonForСollagenDHC12000))
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
            String chatId = update.getCallbackQuery().getMessage().getChatId().toString();
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
                getHtmlCodeWebPage();
                String price = "9200 руб.";
                sendPhoto.setCaption(buttonForСollagenDHC12000.getText() + " за " + price);
                sendPhoto.setPhoto(new InputFile(new File("src/main/resources/data/dhc12000.jpg")));
                sendPhoto.setReplyMarkup(keyboardForButtonForAddCollagenInBasket);
            }

            String strSendPhoto = String.valueOf(sendPhoto);
            System.out.println("Send photo: "+ strSendPhoto);
            int leftIndexForCaption = strSendPhoto.indexOf("caption=") + "caption=".length();
            int rightIndexForCaption = strSendPhoto.indexOf(",", leftIndexForCaption);
            String caption = strSendPhoto.substring(leftIndexForCaption, rightIndexForCaption);
            String availablePhoto = caption.equals("null") ? "да" : "нет";
            System.out.println("Наличие фотографии: " + availablePhoto);
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

    public void getHtmlCodeWebPage() {
        String urlWebPage = "https://kollagen.life/product-category/pitevoj-kollagen";
        String pathToFileWithHtmlCode = "src/main/resources/data/htmlCodeWebPage.html";
        try {
            Document document = Jsoup.connect(urlWebPage).get();
            String strHtmlCode = String.valueOf(document);

            FileWriter fileWriter = new FileWriter(pathToFileWithHtmlCode);
            fileWriter.write(strHtmlCode);
        } catch (Exception ex) {
            ex.getMessage();
        }
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