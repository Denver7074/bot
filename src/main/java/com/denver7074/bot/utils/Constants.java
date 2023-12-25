package com.denver7074.bot.utils;


import java.util.List;
import java.util.regex.Pattern;

public final class Constants {

    public final static String DATE_FORMAT = "dd.MM.yyyy";

    public final static String VERIFICATION = "verification";
    public final static String USER_STATE = "user";

    public final static String TELLEGRAM_URI = "https://api.telegram.org";

    public final static List<String> SORT = List.of("verification_date+desc", "org_title+asc");

    public final static String FGIS_ARCHIN = "https://fgis.gost.ru/fundmetrology/cm/results/";

    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    public static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    public final static String FORMAT_FILE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
}
