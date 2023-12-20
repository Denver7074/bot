package com.denver7074.bot.utils;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.denver7074.bot.utils.Constants.DATE_FORMAT;

@Slf4j
public class Utils {

    @FunctionalInterface
    public interface Supplier<T, E extends Throwable> {
        T get() throws E;
    }

    public static <E> E safeGet(Supplier<E, ? extends Exception> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public static String convertDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern(DATE_FORMAT));
    }

}
