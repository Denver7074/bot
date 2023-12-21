package com.denver7074.bot.utils.errors;

import com.denver7074.bot.model.BotButton;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

import static org.apache.commons.lang3.ObjectUtils.allNull;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum Errors {

    E001("Cущность %s по идентификатору %s не найдена"),
    E002("Прибор с данными %s не найден. Проверьте введенные данные и повторите ввод или вернитесь в главное меню."),
    E003("Неккоректный ввод почты: %s\n Повторите ввод почты."),
    E004("Прибор уже стоит на контроле у вас\n %s \n Повторите поиск или выберите команду из меню."),
    E005("У вас пока нет приборов чтобы выполнить это действие");


    String description;

    public void thr(Boolean isTrue, Long userId, Collection<String> botButton, Object... args) {
        if (BooleanUtils.isFalse(isTrue)) return;
        String errors = allNull(args) ? this.description : String.format(this.description, args);
        throw new CustomException(this, errors, userId, botButton);
    }

    @Getter
    @AllArgsConstructor
    @RequiredArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class CustomException extends RuntimeException {
        final Errors error;
        String message;
        Long userId;
        Collection<String> botButton;

        public String getMsg() {
            return StringUtils.defaultString(message, error.description);
        }

        public String getMessage() {
            return getMsg();
        }

    }

}
