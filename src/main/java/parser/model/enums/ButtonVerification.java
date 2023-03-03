package parser.model.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum ButtonVerification {
    //work_with_verification
    VERIFICATION_UPDATE("\uD83D\uDCDD Редактирование списка СИ"),
    VERIFICATION_SHOW("\uD83D\uDDD3 Просмотр списка CИ"),
    VERIFICATION_FIND("\uD83D\uDD0E Поиск и добавление СИ "),
    EMAIL_NOTIFICATION("\uD83D\uDCE8 Управление e-mail оповещением"),
    //verification_find
    MANUAL_FIND("\uD83D\uDC50 Ручной поиск"),
    AUTO_FIND("⚙️Автоматический поиск"),
    //manual
    NOW_VERIFICATION("⏳ Действующая поверка"),
    HISTORY_VERIFICATION("\uD83D\uDCDC История поверок"),
    //поставка на контроль СИ
    CONTROL_MEASURING_INSTRUMENT("\uD83D\uDCCC Поставить на контроль СИ"),
    NOT_CONTROL_MEASURING_INSTRUMENT("\uD83D\uDDD1 Снять с контроля СИ"),
    //подключение оповещения
    UNPLUG_EMAIL("\uD83D\uDCEA Отключить оповещение"),
    PLUG_EMAIL("\uD83D\uDCEB Подключить оповещение"),
    //редактирование
    UPDATE_AUTO("\uD83D\uDCDD Редактировать название"),
    UPDATE_DAY_MAILING("\uD83D\uDCC6 Изменить дни оповещения"),
    DELETE("\uD83D\uDDD1 Удалить СИ из базы данных."),
    DELETE_AUTO("⚙️Автоматическое удаление"),
    DELETE_MANUAL("\uD83D\uDD79 Удаление по одному"),
    SHOW_MY_INSTRUMENT("\uD83D\uDCC4 Посмотреть конкретное СИ"),
    IMPORT_TO_EXCEL("\uD83D\uDCDA Импорт списка в excel"),
    IMPORT_TO_GOOGLE_SHEET("Импорт в Google sheet");

    String value;
}
