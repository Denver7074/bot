package com.denver7074.bot.utils;


import org.springframework.beans.factory.annotation.Value;

import java.util.List;

public final class Constants {

    public final static String DATE_FORMAT = "dd.MM.yyyy";

    public final static String VERIFICATION = "verification";
    public final static String USER_STATE = "user";

    public final static List<String> SORT = List.of("verification_date+desc", "org_title+asc");

    public final static String FGIS_ARCHIN = "https://fgis.gost.ru/fundmetrology/cm/results/";

    public final static String NO_EMAIL = "\nНет подключенных email адресов.";
}
