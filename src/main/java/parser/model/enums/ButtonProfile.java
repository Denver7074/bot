package parser.model.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum ButtonProfile {
    DELETE_ME("Удалить данные о себе"),
    SHOW_ME_USERS("Показать пользователей"),
    NEXT_PAGE(">>"),
    PREVIOUS_PAGE("<<"),
    YES("Да");

    String value;
}
