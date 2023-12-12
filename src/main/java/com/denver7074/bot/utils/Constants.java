package com.denver7074.bot.utils;


import java.util.List;

public final class Constants {

    public final static String DATE_FORMAT = "dd.MM.yyyy";

    public final static String REDIS_KEY = "userStates";

    public final static List<String> SORT = List.of("verification_date+desc", "org_title+asc");

    public final static String FGIS_ARCHIN = "https://fgis.gost.ru/fundmetrology/cm/results/";
}
