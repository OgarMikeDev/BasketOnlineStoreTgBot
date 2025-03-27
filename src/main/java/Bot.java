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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bot extends TelegramLongPollingBot {
    //Map для хранения товаров каждого клиента
    private static HashMap<Long, List<Collagen>> mapCollagen = new HashMap<>();
    //URL категории жидкого коллагена
    private String urlWebPageWithLiquidCategoryCollagen = "https://kollagen.life/product-category/pitevoj-kollagen";
    //URL категории порошкового коллагена
    private String urlWebPageWithPowderCategoryCollagen = "https://kollagen.life/product-category/kollagen-v-poroshke";
    //URL категории таблетированного коллагена
    private String urlWebPageWithTabletCategoryCollagen = "https://kollagen.life/product-category/kollagen-v-tabletkah";
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
    //Кнопка для коллагена BLACKMORES Collagen
    private InlineKeyboardButton buttonForСollagenBLACKMORES = InlineKeyboardButton.builder()
            .text("BLACKMORES Collagen питьевой 10 x 60 ml")
            .callbackData("drink BLACKMORES")
            .build();
    //Кнопка для коллагена DHC коллаген 9000mg
    private InlineKeyboardButton buttonForСollagenDHC9000Plus = InlineKeyboardButton.builder()
            .text("DHC коллаген 9000mg Plus тетра пак 125 мл x 15")
            .callbackData("drink DHC 9000")
            .build();
    //Кнопка для коллагена DHC нано активного
    private InlineKeyboardButton buttonForСollagenDHCNanoActive = InlineKeyboardButton.builder()
            .text("DHC нано активный коллаген рыбий")
            .callbackData("нано активный коллаген рыбий DHC")
            .build();
    //Клавиатура для питьевых коллагенов
    private InlineKeyboardMarkup keyboardForButtonForLiquidCollagen = InlineKeyboardMarkup.builder()
            .keyboardRow(List.of(buttonForСollagenDHC12000))
            .keyboardRow(List.of(buttonForСollagenShiseidoRelacle))
            .keyboardRow(List.of(buttonForСollagenBLACKMORES))
            .keyboardRow(List.of(buttonForСollagenDHC9000Plus))
            .keyboardRow(List.of(buttonForСollagenDHCNanoActive))
            .keyboardRow(List.of(buttonForReturnBack))
            .build();

    //Кнопка для коллагена Nichie 100%
    private InlineKeyboardButton buttonForСollagenNichie100 = InlineKeyboardButton.builder()
            .text("100% рыбий коллаген пептид Nichie")
            .callbackData("пептид Nichie")
            .build();
    //Кнопка для коллагена DHC 5000mg
    private InlineKeyboardButton buttonForСollagenDHC5000 = InlineKeyboardButton.builder()
            .text("DHC коллаген 5000mg порошок")
            .callbackData("5000 DHC")
            .build();
    //Кнопка для коллагена Fancl Deep Charge collagen в порошке
    private InlineKeyboardButton buttonForСollagenFanclDeepCharge = InlineKeyboardButton.builder()
            .text("Fancl Deep Charge collagen в порошке")
            .callbackData("fancl Deep Charge collagen")
            .build();
    //Кнопка для коллагена Fine Japan Hyaluron & Collagen и коэнзим Q10
    private InlineKeyboardButton buttonForСollagenFineJapanHyaluronCollagenQ10 = InlineKeyboardButton.builder()
            .text("Fine Japan Hyaluron &amp; Collagen и коэнзим Q10")
            .callbackData("fine Japan Hyaluron")
            .build();
    //Кнопка для коллагена Fine Japan Hyaluron & Collagen на 28 дней
    private InlineKeyboardButton buttonForСollagenFineJapanHyaluronCollagen28 = InlineKeyboardButton.builder()
            .text("Fine Japan Hyaluron &amp; Collagen на 28 дней")
            .callbackData("fine Japan Hyaluron 28")
            .build();
    //Кнопка для коллагена Meiji Amino Collagen Matcha Flavor
    private InlineKeyboardButton buttonForСollagenMeijiAminoMatchaFlavor = InlineKeyboardButton.builder()
            .text("Meiji Amino Collagen Matcha Flavor")
            .callbackData("meiji Amino Matcha Flavor")
            .build();
    //Кнопка для коллагена Meiji Amino Collagen Men в порошке
    private InlineKeyboardButton buttonForСollagenMeijiAminoMen = InlineKeyboardButton.builder()
            .text("Meiji Amino Collagen Men в порошке")
            .callbackData("meiji Amino Collagen Men")
            .build();
    //Кнопка для коллагена Meiji Amino Collagen порошок на 28 дней
    private InlineKeyboardButton buttonForСollagenMeijiAmino28 = InlineKeyboardButton.builder()
            .text("Meiji Amino Collagen порошок на 28 дней")
            .callbackData("meiji Amino 28")
            .build();
    //Кнопка для коллагена Meiji Amino Collagen порошок на 30 дней в упаковке
    private InlineKeyboardButton buttonForСollagenMeijiAmino30 = InlineKeyboardButton.builder()
            .text("Meiji Amino Collagen порошок на 30 дней в упаковке")
            .callbackData("meiji Amino 30")
            .build();
    //Кнопка для коллагена Meiji Amino Collagen с кальцием
    private InlineKeyboardButton buttonForСollagenMeijiAminoCalcium = InlineKeyboardButton.builder()
            .text("Meiji Amino Collagen с кальцием")
            .callbackData("meiji Amino с кальцием")
            .build();
    //Кнопка для коллагена Meiji Premium Amino Collagen порошок на 28 дней
    private InlineKeyboardButton buttonForСollagenMeijiAminoPremium28 = InlineKeyboardButton.builder()
            .text("Meiji Premium Amino Collagen порошок на 28 дней")
            .callbackData("meiji Premium Amino 28 дней")
            .build();
    //Кнопка для коллагена Meiji Premium Collagen порошок на 14 дней
    private InlineKeyboardButton buttonForСollagenMeijiPremium14 = InlineKeyboardButton.builder()
            .text("Meiji Premium Collagen порошок на 14 дней")
            .callbackData("meiji Premium 14")
            .build();
    //Кнопка для коллагена Meiji Premium Collagen порошок на 30 дней в упаковке
    private InlineKeyboardButton buttonForСollagenMeijiPremium30 = InlineKeyboardButton.builder()
            .text("Meiji Premium Collagen порошок на 30 дней в упаковке")
            .callbackData("meiji Premium 30")
            .build();
    //Кнопка для коллагена Puruoi Nano Collagen курс 3 недели
    private InlineKeyboardButton buttonForСollagenPuruoiNano3 = InlineKeyboardButton.builder()
            .text("Puruoi Nano Collagen курс 3 недели")
            .callbackData("puruoi Nano 3 недели")
            .build();
    //Кнопка для коллагена Topvalu японский коллаген на 32 дня
    private InlineKeyboardButton buttonForСollagenTopvalu32 = InlineKeyboardButton.builder()
            .text("Topvalu японский коллаген на 32 дня")
            .callbackData("opvalu японский 32 дня")
            .build();
    //Кнопка для коллагена Ururu In One пептид коллагена в порошке
    private InlineKeyboardButton buttonForСollagenUruruInOnePeptide = InlineKeyboardButton.builder()
            .text("Ururu In One пептид коллагена в порошке")
            .callbackData("ururu In One пептид")
            .build();
    //Кнопка для коллагена Wakasapri коллаген и гиалуроновая кислота в саше
    private InlineKeyboardButton buttonForСollagenWakasapriSachet = InlineKeyboardButton.builder()
            .text("Wakasapri коллаген и гиалуроновая кислота в саше")
            .callbackData("wakasapri гиалуроновая кислота в саше")
            .build();
    //Кнопка для коллагена Коллаген Astalift в порошке
    private InlineKeyboardButton buttonForСollagenAstaliftPowder = InlineKeyboardButton.builder()
            .text("Коллаген Astalift в порошке")
            .callbackData("astalift в порошке")
            .build();
    //Кнопка для коллагена Коллаген Nimi из морской рыбы
    private InlineKeyboardButton buttonForСollagenNimiPowder = InlineKeyboardButton.builder()
            .text("Коллаген Nimi из морской рыбы")
            .callbackData("коллаген Nimi из морской рыбы")
            .build();
    //Кнопка для коллагена Коллаген Nimi из пресноводной рыбы
    private InlineKeyboardButton buttonForСollagenNimiFreshwaterPowder = InlineKeyboardButton.builder()
            .text("Коллаген Nimi из пресноводной рыбы")
            .callbackData("коллаген Nimi из пресноводной рыбы")
            .build();
    //Кнопка для коллагена Коллаген Shiseido порошок New
    private InlineKeyboardButton buttonForСollagenShiseidoNewPowder = InlineKeyboardButton.builder()
            .text("Коллаген Shiseido порошок New")
            .callbackData("коллаген Shiseido порошок New")
            .build();
    //Кнопка для коллагена Пептид рыбьего коллагена Green Farm 100 г
    private InlineKeyboardButton buttonForСollagenGreenFarm100Powder = InlineKeyboardButton.builder()
            .text("Пептид рыбьего коллагена Green Farm 100 г")
            .callbackData("пептид рыбьего Green Farm 100")
            .build();
    //Кнопка для коллагена Пептид рыбьего коллагена Green Farm 320 г
    private InlineKeyboardButton buttonForСollagenGreenFarm320Powder = InlineKeyboardButton.builder()
            .text("Пептид рыбьего коллагена Green Farm 320 г")
            .callbackData("пептид рыбьего Green Farm 320")
            .build();
    //Кнопка для коллагена Пептид рыбьего коллагена Nichie 500g
    private InlineKeyboardButton buttonForСollagenNichie500Powder = InlineKeyboardButton.builder()
            .text("Пептид рыбьего коллагена Nichie 500g")
            .callbackData("пептид рыбьего Nichie 500")
            .build();
    //Клавиатура для коллагенов в порошке
    private InlineKeyboardMarkup keyboardForButtonForPowderCollagen = InlineKeyboardMarkup.builder()
            .keyboardRow(List.of(buttonForСollagenNichie100))
            .keyboardRow(List.of(buttonForСollagenDHC5000))
            .keyboardRow(List.of(buttonForСollagenFanclDeepCharge))
            .keyboardRow(List.of(buttonForСollagenFineJapanHyaluronCollagenQ10))
            .keyboardRow(List.of(buttonForСollagenFineJapanHyaluronCollagen28))
            .keyboardRow(List.of(buttonForСollagenMeijiAminoMatchaFlavor))
            .keyboardRow(List.of(buttonForСollagenMeijiAminoMen))
            .keyboardRow(List.of(buttonForСollagenMeijiAmino28))
            .keyboardRow(List.of(buttonForСollagenMeijiAmino30))
            .keyboardRow(List.of(buttonForСollagenMeijiAminoCalcium))
            .keyboardRow(List.of(buttonForСollagenMeijiAminoPremium28))
            .keyboardRow(List.of(buttonForСollagenMeijiPremium14))
            .keyboardRow(List.of(buttonForСollagenMeijiPremium30))
            .keyboardRow(List.of(buttonForСollagenPuruoiNano3))
            .keyboardRow(List.of(buttonForСollagenTopvalu32))
            .keyboardRow(List.of(buttonForСollagenUruruInOnePeptide))
            .keyboardRow(List.of(buttonForСollagenWakasapriSachet))
            .keyboardRow(List.of(buttonForСollagenAstaliftPowder))
            .keyboardRow(List.of(buttonForСollagenNimiPowder))
            .keyboardRow(List.of(buttonForСollagenNimiFreshwaterPowder))
            .keyboardRow(List.of(buttonForСollagenShiseidoNewPowder))
            .keyboardRow(List.of(buttonForСollagenGreenFarm100Powder))
            .keyboardRow(List.of(buttonForСollagenGreenFarm320Powder))
            .keyboardRow(List.of(buttonForСollagenNichie500Powder))
            .keyboardRow(List.of(buttonForReturnBack))
            .build();

    //Кнопка для коллагена Asahi Dear Natura коллаген в таблетках
    private InlineKeyboardButton buttonForСollagenAsahiDearNatura = InlineKeyboardButton.builder()
            .text("Asahi Dear Natura коллаген в таблетках")
            .callbackData("asahi Dear Natura")
            .build();
    //Кнопка для коллагена DHC коллаген в таблетках на 60 дней
    private InlineKeyboardButton buttonForСollagenDHC60Tablet = InlineKeyboardButton.builder()
            .text("DHC коллаген в таблетках на 60 дней")
            .callbackData("dhc таблетки 60 дней")
            .build();
    //Кнопка для коллагена Fancl Deep Charge collagen в таблетках
    private InlineKeyboardButton buttonForСollagenFanclDeepChargeTablet  = InlineKeyboardButton.builder()
            .text("Fancl Deep Charge collagen в таблетках")
            .callbackData("fancl Deep Charge collagen таблетки")
            .build();
    //Кнопка для коллагена Shiseido Enriched коллаген таблетки
    private InlineKeyboardButton buttonForСollagenShiseidoEnrichedTablet = InlineKeyboardButton.builder()
            .text("Shiseido Enriched коллаген таблетки")
            .callbackData("shiseido Enriched коллаген таблетки")
            .build();
    //Кнопка для коллагена Shiseido EX коллаген в таблетках
    private InlineKeyboardButton buttonForСollagenShiseidoEXTablet = InlineKeyboardButton.builder()
            .text("Shiseido EX коллаген в таблетках")
            .callbackData("shiseido EX коллаген в таблетках")
            .build();
    //Кнопка для коллагена Shiseido Relacle коллаген в таблетках
    private InlineKeyboardButton buttonForСollagenShiseidoRelacleTablet = InlineKeyboardButton.builder()
            .text("Shiseido Relacle коллаген в таблетках")
            .callbackData("shiseido Relacle таблетки")
            .build();
    //Кнопка для коллагена Shiseido Rich Rich коллаген в таблетках
    private InlineKeyboardButton buttonForСollagenShiseidoRichRichTablet = InlineKeyboardButton.builder()
            .text("Shiseido Rich Rich коллаген в таблетках")
            .callbackData("shiseido Rich Rich таблетки")
            .build();
    //Кнопка для коллагена Shiseido The Collagen EXR в таблетках
    private InlineKeyboardButton buttonForСollagenShiseidoTheEXRTablet = InlineKeyboardButton.builder()
            .text("Shiseido The Collagen EXR в таблетках")
            .callbackData("shiseido The Collagen EXR таблетки")
            .build();
    //Кнопка для коллагена Total Image коллаген 20 таблеток
    private InlineKeyboardButton buttonForСollagenTotalImage20Tablet = InlineKeyboardButton.builder()
            .text("Total Image коллаген 20 таблеток")
            .callbackData("total Image 20 таблетки")
            .build();
    //Кнопка для коллагена Коллаген Shiseido The Collagen в таблетках
    private InlineKeyboardButton buttonForСollagenShiseidoTheTablet = InlineKeyboardButton.builder()
            .text("Коллаген Shiseido The Collagen в таблетках")
            .callbackData("shiseido the collagen 20 таблетки")
            .build();
    //Кнопка для коллагена Коллаген в капсулах Maruman 15000 мг
    private InlineKeyboardButton buttonForСollagenMarumanTablet = InlineKeyboardButton.builder()
            .text("Коллаген в капсулах Maruman 15000 мг")
            .callbackData("maruman таблетки")
            .build();
    //Клавиатура для кнопки для коллагена DHC 12000mg
    private InlineKeyboardMarkup keyboardForButtonForTabletCollagen = InlineKeyboardMarkup.builder()
            .keyboardRow(List.of(buttonForСollagenAsahiDearNatura))
            .keyboardRow(List.of(buttonForСollagenDHC60Tablet))
            .keyboardRow(List.of(buttonForСollagenFanclDeepChargeTablet))
            .keyboardRow(List.of(buttonForСollagenShiseidoEnrichedTablet))
            .keyboardRow(List.of(buttonForСollagenShiseidoEXTablet))
            .keyboardRow(List.of(buttonForСollagenShiseidoRelacleTablet))
            .keyboardRow(List.of(buttonForСollagenShiseidoRichRichTablet))
            .keyboardRow(List.of(buttonForСollagenShiseidoTheEXRTablet))
            .keyboardRow(List.of(buttonForСollagenTotalImage20Tablet))
            .keyboardRow(List.of(buttonForСollagenShiseidoTheTablet))
            .keyboardRow(List.of(buttonForСollagenMarumanTablet))
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
            } else if (callbackData.equals(buttonForPowderCollagen.getCallbackData())) {
                editMessageText.setText("Коллаген в порошке");
                editMessageReplyMarkup.setReplyMarkup(keyboardForButtonForPowderCollagen);
            } else if (callbackData.equals(buttonForTabletCollagen.getCallbackData())) {
                editMessageText.setText("Коллаген в таблетках");
                editMessageReplyMarkup.setReplyMarkup(keyboardForButtonForTabletCollagen);
            }
            //Питьевой коллаген
            else if (callbackData.equals(buttonForСollagenDHC12000.getCallbackData())) {
                currentPriceCollagen = forGetPriceCollagenWithSelectedCategory(buttonForСollagenDHC12000.getText(), urlWebPageWithLiquidCategoryCollagen);
                currentNameCollagen = buttonForСollagenDHC12000.getText();
                sendPhoto.setCaption(currentNameCollagen + " за " + currentPriceCollagen + " руб.");
                sendPhoto.setPhoto(new InputFile(new File("src/main/resources/data/dhc12000.jpg")));
                sendPhoto.setReplyMarkup(keyboardForButtonForAddCollagenInBasket);
            } else if (callbackData.equals(buttonForСollagenShiseidoRelacle.getCallbackData())) {
                currentPriceCollagen = forGetPriceCollagenWithSelectedCategory(buttonForСollagenShiseidoRelacle.getText(), urlWebPageWithLiquidCategoryCollagen);
                currentNameCollagen = buttonForСollagenShiseidoRelacle.getText();
                sendPhoto.setCaption(currentNameCollagen + " за " + currentPriceCollagen + " руб.");
                sendPhoto.setPhoto(new InputFile(new File("src/main/resources/data/heseido_relacle_collagen_pitevoy.jpg")));
                sendPhoto.setReplyMarkup(keyboardForButtonForAddCollagenInBasket);
            } else if (callbackData.equals(buttonForСollagenBLACKMORES.getCallbackData())) {
                currentPriceCollagen = forGetPriceCollagenWithSelectedCategory(buttonForСollagenBLACKMORES.getText(), urlWebPageWithLiquidCategoryCollagen);
                currentNameCollagen = buttonForСollagenBLACKMORES.getText();
                sendPhoto.setCaption(currentNameCollagen + " за " + currentPriceCollagen + " руб.");
                sendPhoto.setPhoto(new InputFile(new File("src/main/resources/data/BLACKMORES.jpg")));
                sendPhoto.setReplyMarkup(keyboardForButtonForAddCollagenInBasket);
            } else if (callbackData.equals(buttonForСollagenDHC9000Plus.getCallbackData())) {
                currentPriceCollagen = forGetPriceCollagenWithSelectedCategory(buttonForСollagenDHC9000Plus.getText(), urlWebPageWithLiquidCategoryCollagen);
                currentNameCollagen = buttonForСollagenDHC9000Plus.getText();
                sendPhoto.setCaption(currentNameCollagen + " за " + currentPriceCollagen + " руб.");
                sendPhoto.setPhoto(new InputFile(new File("src/main/resources/data/DHC-Collagen-Beauty-9000-Plus-tetra.jpg")));
                sendPhoto.setReplyMarkup(keyboardForButtonForAddCollagenInBasket);
            } else if (callbackData.equals(buttonForСollagenDHCNanoActive.getCallbackData())) {
                currentPriceCollagen = forGetPriceCollagenWithSelectedCategory(buttonForСollagenDHCNanoActive.getText(), urlWebPageWithLiquidCategoryCollagen);
                currentNameCollagen = buttonForСollagenDHCNanoActive.getText();
                sendPhoto.setCaption(currentNameCollagen + " за " + currentPriceCollagen + " руб.");
                sendPhoto.setPhoto(new InputFile(new File("src/main/resources/data/DHC-nano-active-collagen-new1.jpg")));
                sendPhoto.setReplyMarkup(keyboardForButtonForAddCollagenInBasket);
            }
            //Коллаген в порошке
            else if (callbackData.equals(buttonForСollagenNichie100.getCallbackData())) {
                currentPriceCollagen = forGetPriceCollagenWithSelectedCategory(buttonForСollagenNichie100.getText(), urlWebPageWithPowderCategoryCollagen);
                currentNameCollagen = buttonForСollagenNichie100.getText();
                sendPhoto.setCaption(currentNameCollagen + " за " + currentPriceCollagen + " руб.");
                sendPhoto.setPhoto(new InputFile(new File("src/main/resources/data/nichie-new-pack.jpg")));
                sendPhoto.setReplyMarkup(keyboardForButtonForAddCollagenInBasket);
            } else if (callbackData.equals(buttonForСollagenDHC5000.getCallbackData())) {
                currentPriceCollagen = forGetPriceCollagenWithSelectedCategory(buttonForСollagenDHC5000.getText(), urlWebPageWithPowderCategoryCollagen);
                currentNameCollagen = buttonForСollagenDHC5000.getText();
                sendPhoto.setCaption(currentNameCollagen + " за " + currentPriceCollagen + " руб.");
                sendPhoto.setPhoto(new InputFile(new File("src/main/resources/data/DHC коллаген 5000mg порошок.png")));
                sendPhoto.setReplyMarkup(keyboardForButtonForAddCollagenInBasket);
            } else if (callbackData.equals(buttonForСollagenFanclDeepCharge.getCallbackData())) {
                currentPriceCollagen = forGetPriceCollagenWithSelectedCategory(buttonForСollagenFanclDeepCharge.getText(), urlWebPageWithPowderCategoryCollagen);
                currentNameCollagen = buttonForСollagenFanclDeepCharge.getText();
                sendPhoto.setCaption(currentNameCollagen + " за " + currentPriceCollagen + " руб.");
                sendPhoto.setPhoto(new InputFile(new File("src/main/resources/data/fancl-deep-charge.jpg")));
                sendPhoto.setReplyMarkup(keyboardForButtonForAddCollagenInBasket);
            } else if (callbackData.equals(buttonForСollagenFineJapanHyaluronCollagenQ10.getCallbackData())) {
                currentPriceCollagen = forGetPriceCollagenWithSelectedCategory(buttonForСollagenFineJapanHyaluronCollagenQ10.getText(), urlWebPageWithPowderCategoryCollagen);
                currentNameCollagen = buttonForСollagenFineJapanHyaluronCollagenQ10.getText();
                sendPhoto.setCaption(currentNameCollagen + " за " + currentPriceCollagen + " руб.");
                sendPhoto.setPhoto(new InputFile(new File("src/main/resources/data/Fine.webp")));
                sendPhoto.setReplyMarkup(keyboardForButtonForAddCollagenInBasket);
            } else if (callbackData.equals(buttonForСollagenFineJapanHyaluronCollagen28.getCallbackData())) {
                currentPriceCollagen = forGetPriceCollagenWithSelectedCategory(buttonForСollagenFineJapanHyaluronCollagen28.getText(), urlWebPageWithPowderCategoryCollagen);
                currentNameCollagen = buttonForСollagenFineJapanHyaluronCollagen28.getText();
                sendPhoto.setCaption(currentNameCollagen + " за " + currentPriceCollagen + " руб.");
                sendPhoto.setPhoto(new InputFile(new File("src/main/resources/data/xCollagen-fine-gold-hyaluron-and-collagen-can1-300x300.jpg.pagespeed.ic.UF76UORtGX.jpg")));
                sendPhoto.setReplyMarkup(keyboardForButtonForAddCollagenInBasket);
            } else if (callbackData.equals(buttonForСollagenMeijiAminoMatchaFlavor.getCallbackData())) {
                currentPriceCollagen = forGetPriceCollagenWithSelectedCategory(buttonForСollagenMeijiAminoMatchaFlavor.getText(), urlWebPageWithPowderCategoryCollagen);
                currentNameCollagen = buttonForСollagenMeijiAminoMatchaFlavor.getText();
                sendPhoto.setCaption(currentNameCollagen + " за " + currentPriceCollagen + " руб.");
                sendPhoto.setPhoto(new InputFile(new File("src/main/resources/data/Meiji Amino Collagen Matcha Flavor.webp")));
                sendPhoto.setReplyMarkup(keyboardForButtonForAddCollagenInBasket);
            } else if (callbackData.equals(buttonForСollagenMeijiAminoMen.getCallbackData())) {
                currentPriceCollagen = forGetPriceCollagenWithSelectedCategory(buttonForСollagenMeijiAminoMen.getText(), urlWebPageWithPowderCategoryCollagen);
                currentNameCollagen = buttonForСollagenMeijiAminoMen.getText();
                sendPhoto.setCaption(currentNameCollagen + " за " + currentPriceCollagen + " руб.");
                sendPhoto.setPhoto(new InputFile(new File("src/main/resources/data/Meiji Amino Collagen Men в порошке.webp")));
                sendPhoto.setReplyMarkup(keyboardForButtonForAddCollagenInBasket);
            } else if (callbackData.equals(buttonForСollagenMeijiAmino28.getCallbackData())) {
                currentPriceCollagen = forGetPriceCollagenWithSelectedCategory(buttonForСollagenMeijiAmino28.getText(), urlWebPageWithPowderCategoryCollagen);
                currentNameCollagen = buttonForСollagenMeijiAmino28.getText();
                sendPhoto.setCaption(currentNameCollagen + " за " + currentPriceCollagen + " руб.");
                sendPhoto.setPhoto(new InputFile(new File("src/main/resources/data/Meiji Amino Collagen порошок на 28 дней.webp")));
                sendPhoto.setReplyMarkup(keyboardForButtonForAddCollagenInBasket);
            } else if (callbackData.equals(buttonForСollagenMeijiAmino30.getCallbackData())) {
                currentPriceCollagen = forGetPriceCollagenWithSelectedCategory(buttonForСollagenMeijiAmino30.getText(), urlWebPageWithPowderCategoryCollagen);
                currentNameCollagen = buttonForСollagenMeijiAmino30.getText();
                sendPhoto.setCaption(currentNameCollagen + " за " + currentPriceCollagen + " руб.");
                sendPhoto.setPhoto(new InputFile(new File("src/main/resources/data/Meiji Amino Collagen порошок на 30 дней в упаковке.webp")));
                sendPhoto.setReplyMarkup(keyboardForButtonForAddCollagenInBasket);
            } else if (callbackData.equals(buttonForСollagenMeijiAminoCalcium.getCallbackData())) {
                currentPriceCollagen = forGetPriceCollagenWithSelectedCategory(buttonForСollagenMeijiAminoCalcium.getText(), urlWebPageWithPowderCategoryCollagen);
                currentNameCollagen = buttonForСollagenMeijiAminoCalcium.getText();
                sendPhoto.setCaption(currentNameCollagen + " за " + currentPriceCollagen + " руб.");
                sendPhoto.setPhoto(new InputFile(new File("src/main/resources/data/Meiji Amino Collagen с кальцием.webp")));
                sendPhoto.setReplyMarkup(keyboardForButtonForAddCollagenInBasket);
            } else if (callbackData.equals(buttonForСollagenMeijiAminoPremium28.getCallbackData())) {
                currentPriceCollagen = forGetPriceCollagenWithSelectedCategory(buttonForСollagenMeijiAminoPremium28.getText(), urlWebPageWithPowderCategoryCollagen);
                currentNameCollagen = buttonForСollagenMeijiAminoPremium28.getText();
                sendPhoto.setCaption(currentNameCollagen + " за " + currentPriceCollagen + " руб.");
                sendPhoto.setPhoto(new InputFile(new File("src/main/resources/data/Meiji Premium Amino Collagen порошок на 28 дней.webp")));
                sendPhoto.setReplyMarkup(keyboardForButtonForAddCollagenInBasket);
            } else if (callbackData.equals(buttonForСollagenMeijiPremium14.getCallbackData())) {
                currentPriceCollagen = forGetPriceCollagenWithSelectedCategory(buttonForСollagenMeijiPremium14.getText(), urlWebPageWithPowderCategoryCollagen);
                currentNameCollagen = buttonForСollagenMeijiPremium14.getText();
                sendPhoto.setCaption(currentNameCollagen + " за " + currentPriceCollagen + " руб.");
                sendPhoto.setPhoto(new InputFile(new File("src/main/resources/data/Meiji Premium Collagen порошок на 14 дней.webp")));
                sendPhoto.setReplyMarkup(keyboardForButtonForAddCollagenInBasket);
            }
            //Коллаген в таблетках
            else if (callbackData.equals(buttonForСollagenAsahiDearNatura.getCallbackData())) {
                currentPriceCollagen = forGetPriceCollagenWithSelectedCategory(buttonForСollagenAsahiDearNatura.getText(), urlWebPageWithTabletCategoryCollagen);
                currentNameCollagen = buttonForСollagenAsahiDearNatura.getText();
                sendPhoto.setCaption(currentNameCollagen + " за " + currentPriceCollagen + " руб.");
                sendPhoto.setPhoto(new InputFile(new File("src/main/resources/data/xAsahi-dear-natura-collagen-300x300.jpg.pagespeed.ic.MSiksS3paR.webp")));
                sendPhoto.setReplyMarkup(keyboardForButtonForAddCollagenInBasket);
            } else if (callbackData.equals(buttonForСollagenDHC60Tablet.getCallbackData())) {
                currentPriceCollagen = forGetPriceCollagenWithSelectedCategory(buttonForСollagenDHC60Tablet.getText(), urlWebPageWithTabletCategoryCollagen);
                currentNameCollagen = buttonForСollagenDHC60Tablet.getText();
                sendPhoto.setCaption(currentNameCollagen + " за " + currentPriceCollagen + " руб.");
                sendPhoto.setPhoto(new InputFile(new File("src/main/resources/data/xdhc_collagen_tablets1-300x300.jpg.pagespeed.ic.8TlBiiHjrj (1).webp")));
                sendPhoto.setReplyMarkup(keyboardForButtonForAddCollagenInBasket);
            } else if (callbackData.equals(buttonForСollagenFanclDeepChargeTablet.getCallbackData())) {
                currentPriceCollagen = forGetPriceCollagenWithSelectedCategory(buttonForСollagenFanclDeepChargeTablet.getText(), urlWebPageWithTabletCategoryCollagen);
                currentNameCollagen = buttonForСollagenFanclDeepChargeTablet.getText();
                sendPhoto.setCaption(currentNameCollagen + " за " + currentPriceCollagen + " руб.");
                sendPhoto.setPhoto(new InputFile(new File("src/main/resources/data/xfancl-deep-charge-collagen-tablets-new-300x300.jpg.pagespeed.ic.0ySqu1z8r_.jfif")));
                sendPhoto.setReplyMarkup(keyboardForButtonForAddCollagenInBasket);
            } else if (callbackData.equals(buttonForСollagenShiseidoEnrichedTablet.getCallbackData())) {
                currentPriceCollagen = forGetPriceCollagenWithSelectedCategory(buttonForСollagenShiseidoEnrichedTablet.getText(), urlWebPageWithTabletCategoryCollagen);
                currentNameCollagen = buttonForСollagenShiseidoEnrichedTablet.getText();
                sendPhoto.setCaption(currentNameCollagen + " за " + currentPriceCollagen + " руб.");
                sendPhoto.setPhoto(new InputFile(new File("src/main/resources/data/xshiseido_collagen_enriched_tablets-300x300.jpg.pagespeed.ic.JDW0bJfUWN.webp")));
                sendPhoto.setReplyMarkup(keyboardForButtonForAddCollagenInBasket);
            } else if (callbackData.equals(buttonForСollagenShiseidoEXTablet.getCallbackData())) {
                currentPriceCollagen = forGetPriceCollagenWithSelectedCategory(buttonForСollagenShiseidoEXTablet.getText(), urlWebPageWithTabletCategoryCollagen);
                currentNameCollagen = buttonForСollagenShiseidoEXTablet.getText();
                sendPhoto.setCaption(currentNameCollagen + " за " + currentPriceCollagen + " руб.");
                sendPhoto.setPhoto(new InputFile(new File("src/main/resources/data/xshiseido_collagen_ex_tablets-300x300.jpg.pagespeed.ic.bctUc0r1wK.webp")));
                sendPhoto.setReplyMarkup(keyboardForButtonForAddCollagenInBasket);
            } else if (callbackData.equals(buttonForСollagenShiseidoRelacleTablet.getCallbackData())) {
                currentPriceCollagen = forGetPriceCollagenWithSelectedCategory(buttonForСollagenShiseidoRelacleTablet.getText(), urlWebPageWithTabletCategoryCollagen);
                currentNameCollagen = buttonForСollagenShiseidoRelacleTablet.getText();
                sendPhoto.setCaption(currentNameCollagen + " за " + currentPriceCollagen + " руб.");
                sendPhoto.setPhoto(new InputFile(new File("src/main/resources/data/Shiseido Relacle коллаген в таблетках.webp")));
                sendPhoto.setReplyMarkup(keyboardForButtonForAddCollagenInBasket);
            } else if (callbackData.equals(buttonForСollagenShiseidoRichRichTablet.getCallbackData())) {
                currentPriceCollagen = forGetPriceCollagenWithSelectedCategory(buttonForСollagenShiseidoRichRichTablet.getText(), urlWebPageWithTabletCategoryCollagen);
                currentNameCollagen = buttonForСollagenShiseidoRichRichTablet.getText();
                sendPhoto.setCaption(currentNameCollagen + " за " + currentPriceCollagen + " руб.");
                sendPhoto.setPhoto(new InputFile(new File("src/main/resources/data/Shiseido Rich Rich коллаген в таблетках.webp")));
                sendPhoto.setReplyMarkup(keyboardForButtonForAddCollagenInBasket);
            } else if (callbackData.equals(buttonForСollagenShiseidoTheEXRTablet.getCallbackData())) {
                currentPriceCollagen = forGetPriceCollagenWithSelectedCategory(buttonForСollagenShiseidoTheEXRTablet.getText(), urlWebPageWithTabletCategoryCollagen);
                currentNameCollagen = buttonForСollagenShiseidoTheEXRTablet.getText();
                sendPhoto.setCaption(currentNameCollagen + " за " + currentPriceCollagen + " руб.");
                sendPhoto.setPhoto(new InputFile(new File("src/main/resources/data/Shiseido The Collagen EXR в таблетках.webp")));
                sendPhoto.setReplyMarkup(keyboardForButtonForAddCollagenInBasket);
            } else if (callbackData.equals(buttonForСollagenTotalImage20Tablet.getCallbackData())) {
                currentPriceCollagen = forGetPriceCollagenWithSelectedCategory(buttonForСollagenTotalImage20Tablet.getText(), urlWebPageWithTabletCategoryCollagen);
                currentNameCollagen = buttonForСollagenTotalImage20Tablet.getText();
                sendPhoto.setCaption(currentNameCollagen + " за " + currentPriceCollagen + " руб.");
                sendPhoto.setPhoto(new InputFile(new File("src/main/resources/data/Total Image коллаген 20 таблеток.webp")));
                sendPhoto.setReplyMarkup(keyboardForButtonForAddCollagenInBasket);
            } else if (callbackData.equals(buttonForСollagenShiseidoTheTablet.getCallbackData())) {
                currentPriceCollagen = forGetPriceCollagenWithSelectedCategory(buttonForСollagenShiseidoTheTablet.getText(), urlWebPageWithTabletCategoryCollagen);
                currentNameCollagen = buttonForСollagenShiseidoTheTablet.getText();
                sendPhoto.setCaption(currentNameCollagen + " за " + currentPriceCollagen + " руб.");
                sendPhoto.setPhoto(new InputFile(new File("src/main/resources/data/Коллаген Shiseido The Collagen в таблетках.webp")));
                sendPhoto.setReplyMarkup(keyboardForButtonForAddCollagenInBasket);
            } else if (callbackData.equals(buttonForСollagenMarumanTablet.getCallbackData())) {
                currentPriceCollagen = forGetPriceCollagenWithSelectedCategory(buttonForСollagenMarumanTablet.getText(), urlWebPageWithTabletCategoryCollagen);
                currentNameCollagen = buttonForСollagenMarumanTablet.getText();
                sendPhoto.setCaption(currentNameCollagen + " за " + currentPriceCollagen + " руб.");
                sendPhoto.setPhoto(new InputFile(new File("src/main/resources/data/Коллаген в капсулах Maruman 15000 мг.webp")));
                sendPhoto.setReplyMarkup(keyboardForButtonForAddCollagenInBasket);
            } else if (callbackData.equals(buttonForMyBasket.getCallbackData())) {
                for (Map.Entry<Long, List<Collagen>> allCollagen : mapCollagen.entrySet()) {
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
                List<Collagen> listCollagen = mapCollagen.get(chatId);
                if (listCollagen == null) {
                    listCollagen = new ArrayList<>();
                    listCollagen.add(new Collagen(currentNameCollagen, currentPriceCollagen));
                    mapCollagen.put(chatId, listCollagen);
                } else {
                    listCollagen = mapCollagen.get(chatId);
                    listCollagen.add(currentCollagen);
                    mapCollagen.put(chatId, listCollagen);
                }
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
            Document document = Jsoup.connect(urlWebPageWithCategoryCollagen).get();
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