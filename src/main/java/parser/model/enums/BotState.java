package parser.model.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.*;

//возможное состояние бота
@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum BotState {

    ABOUT_ME("Мой профиль:\n"),
    //work_with_verification
    VERIFICATION_FIND("Каким способ хотите осуществить поиск?"),
    VERIFICATION_SHOW("Что вас интересует?"),
    VERIFICATION_UPDATE("Что хотите изменить?"),
    EMAIL_NOTIFICATION("Что желаете?"),
    //verification_find
    FIND_MANUAL("Что хотите найти?"),
    FIND_AUTO("Скачайте шаблон excel, заполните обязательные поля,а именно заводской номер и номер ГРСИ. Можете заполнить название СИ."),
    DELETE("Выберете способ удаления."),
    DELETE_MANUAL("Введите номер ГРСИ и заводской номер через пробел\n" + "Пример ввода: 51124-12 191"),
    DELETE_AUTO("Скачайте шаблон excel, заполните обязательные поля,а именно заводской номер и номер ГРСИ."),
    UPDATE_AUTO("Вы можете изменить только название оборудование, если оно было загружено из ФГИС Аршин некорректно.\n" +
            "Скачайте шаблон excel, заполните обязательные поля,а именно заводской номер и номер ГРСИ. Введите корректное название СИ."),
    UPDATE_DAY_MAILING("Для изменения количество дней, за сколько до окончания поверке должно приходить оповещение, то ответным сообщением отправьте целое число дней или выберете другую команду из меню. В настоящий момент установлено дней(я):"),
    //manual_find
    NOW_VERIFICATION("Введите номер ГРСИ и заводской номер через пробел\n" + "Пример ввода: 51124-12 191"),
    HISTORY_VERIFICATION("История поверок на конкретный прибор. Введите номер ГРСИ и заводской номер через пробел\n" + "Пример ввода: 51124-12 191"),
    NOT_FOUND_VERIFICATION("Поверка не найдена. Данные введены некорректно или такого СИ не существует. Выберете заново действие или выберете другие действия в menu." + "\n" + "Пример ввода: 51124-12 191"),
    //поставка на контроль СИ
    CONTROL_MEASURING_INSTRUMENT("Прибор поставлен на контроль. Если хотите изменить, за сколько дней до окончания поверке должно приходить оповещение, то ответным сообщением отправьте целое число дней или выберете другую команду из меню. В настоящий момент установлено дней(я): "),
    NOT_CONTROL_MEASURING_INSTRUMENT("Прибор снят с контроля. Больше оповещения по данному прибору не будут вас тревожить."),
    DELETE_VERIFICATION("СИ снято с контроля"),
    UNPLUG_EMAIL("Для отключение оповещения перечислите через запятую e-mail адреса через запятую.На данный момент у вас подключено оповещение к следующим e-mail адресам:\n"),
    PLUG_EMAIL("Введите через запятую все e-mail адреса, на которые вы хотите уведомления. На данный момент подключены следующие e-mail адреса:\n"),
    IMPORT_TO_EXCEL("Импорт списка"),
    IMPORT_TO_GOOGLE_SHEET("Импорт в Google sheet"),
    SHOW_MY_INSTRUMENT("Введите номер ГРСИ и заводской номер через пробел\n" + "Пример ввода: 51124-12 191"),
    SHOW_ALL_SI("Импортировать весь список приборов"),
    //menu
    WORK_WITH_VERIFICATION("В данном разделе Поверка СИ бот позволит вам провести организовать следующие:"),
    SHOW_MAIN_MENU("Выберете интересующую вас команду из Меню \uD83D\uDCCB или нажмите /help \uD83C\uDD98 для ознакомления со списком команд"),
    SHOW_HELP_MENU("Бот поможет сделать следующее:\n" +
            "/verification поиск поверке СИ \uD83D\uDD0D\n\n"),

    //Раздел "Реклама"
    ADVERTISEMENT("В данном разделе вы можете предложить свои услуги и товары для лабораторий.\n" +
            "Введите текст и прикрепите все приложения связанные с вашей услугой."),
    //Раздел "Аудит"
    AUDIT("В данном разделе вы сможете провести внешний аудит по своим документам.  Выберете какой аудит вы хотите провести?"),
    SHOW_ME_USERS("Просмотр пользователей"),
    SOFT_DELETE_USER("У вас есть возможность восстановиться вместе с последними данными в течении 180 дней, иначе ваши данные удаляться бесследно."),
    DELETE_ME("Вы уверены что хотите удалить свои данные из телеграмм бота? При удалении вашу учетную запись можно будет восстановить в течении 180 дней.\n Далее все данные безвозвратно удаляться. Если нет, то выберете другие команды из списка меню.");

    String value;

    public static final EnumSet<BotState> callBack = EnumSet.copyOf(
            List.of(IMPORT_TO_EXCEL, FIND_AUTO, UPDATE_AUTO, DELETE_AUTO));

    public static final EnumSet<BotState> handleCallback = EnumSet.copyOf(
            List.of(VERIFICATION_FIND, FIND_MANUAL, VERIFICATION_SHOW, VERIFICATION_UPDATE, DELETE, EMAIL_NOTIFICATION,DELETE_ME,SHOW_ME_USERS));
    public static final EnumSet<BotState> sendMessage = EnumSet.copyOf(
            List.of(WORK_WITH_VERIFICATION, NOW_VERIFICATION, HISTORY_VERIFICATION, SHOW_MY_INSTRUMENT, DELETE_MANUAL, ABOUT_ME));
}
