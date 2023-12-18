package com.denver7074.bot.model;

import com.denver7074.bot.service.response.SendMsg;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum BotState {
    // старт
    SHOW_START_MENU("Привет, %s! \n Выберете интересующую вас команду из Меню \uD83D\uDCCB " +
            "или нажмите /help \uD83C\uDD98 для ознакомления со списком команд"),
    //Главное меню
    WORK_WITH_VERIFICATION("В данном разделе 'Поверка СИ' бот " +
            "позволит вам организовать следующие:"),
    SHOW_HELP_MENU("У бота есть следующие команды\n " +
            "/start главное меню\n" +
            "/profile посмотреть свой профиль\n" +
            " /verification \uD83D\uDD0D поиск поверки СИ"),
    ABOUT_ME("Мой профиль:\n %s"),
    DEFAULT("Я тебя не понимаю"),

    //WORK_WITH_VERIFICATION меню
    VERIFICATION_FIND("Введите номер ГРСИ и заводской номер через пробел или скачайте шаблон, " +
            "заполните обязательные поля и загрузите обратным сообщением.\n Пример ввода: 51124-12 191"),
    VERIFICATION_SHOW("Просмотр всех СИ закрепленнх за пользователем:"),
    //вот здесь нужно выгрузить все средства измерения в excel и на основе этой таблицы внести правки
    VERIFICATION_UPDATE("Вы можете изменить только название оборудование, если оно было загружено из ФГИС Аршин некорректно.\n" +
                        "Скачайте шаблон excel, введите корректное название СИ и ответным сообщением отправьте шаблон с исправленными названиями."),
    EMAIL_NOTIFICATION("Укажите email адреса на которые необходимо производить рассылку " +
            "или нажмите на кнопу с адресом, который вы хотите исключить из рассылки."),
    //VERIFICATION_FIND
    SAVE_VERIFICATION("%s \n Нажмите \"Поставить на контроль\" если хотите получать оповещения о необходимости поверки или продолжайте ручной поиск." +
            "\n Пример ввода: 51124-12 191"),
    VERIFICATION_SAVE("СИ поставленно на контроль. Выберите дальнейшую команд из меню или продолжайте поиск." +
                      "\n Пример ввода: 51124-12 191");

    public static final EnumSet<BotState> botStates = EnumSet.of(
            VERIFICATION_FIND, SAVE_VERIFICATION, EMAIL_NOTIFICATION
    );

    String message;

    public SendMessage sendMessage(Subscriber user, Collection<String> buttons, Object... args) {
        user.setBotState(this);
        String textMsg = ObjectUtils.allNull(args) ? this.message : String.format(this.message, args);
        return new SendMsg(textMsg, user.getId(), buttons).getMsg();
    }

    public EditMessageText sendMessage(Subscriber user, List<String> buttons, Integer messageId, Object... args) {
        user.setBotState(this);
        String textMsg = ObjectUtils.allNull(args) ? this.message : String.format(this.message, args);
        return new SendMsg(textMsg, user.getId(), buttons, messageId).getEditMsg();
    }

    public SendDocument sendMessage(Subscriber user, InputStream file, Object... args) {
        user.setBotState(this);
        String textMsg = ObjectUtils.allNull(args) ? this.message : String.format(this.message, args);
        return new SendMsg(textMsg, user.getId(), file).getDocument();
    }


}
