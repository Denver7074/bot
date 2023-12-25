package com.denver7074.bot.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import static com.denver7074.bot.utils.Constants.TELLEGRAM_URI;


@Data
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BotConfig {

    String botName;
    String botToken;
    String fileInfo;
    String fileStore;
    String userName;
    String password;
    String yandexKey;

    @JsonProperty("botToken")
    public void setBotToken(String botToken) {
        this.botToken = botToken;
        this.fileInfo = TELLEGRAM_URI + "/bot" + botToken + "/getFile?file_id=";
        this.fileStore = TELLEGRAM_URI + "/file/bot" + botToken +"/";
    }

}
