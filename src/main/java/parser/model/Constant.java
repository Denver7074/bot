package parser.model;


import lombok.experimental.UtilityClass;

import java.time.format.DateTimeFormatter;

@UtilityClass
public class Constant {

    public String HREF = "https://fgis.gost.ru/fundmetrology/cm/results/";
    public DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    public String POST_URL_SPEECH = "https://stt.api.cloud.yandex.net/speech/v1/stt:recognize";
    public String ADMIN_ID = "806820596";
    public String API_KEY_YANDEX_CLOUD = "Api-Key AQVNz0cwlgtJsXWkuTWO5qx7_XqUbbIpbQDv3lCc";
    public String FIRST_ANSWER = "Действие поверки заканчивается‼️";
    public String UPDATE_ANSWER = "Данные по поверке обновились‼️";
}
