package com.denver7074.bot.service.messageservice;

import com.denver7074.bot.configuration.BotConfig;
import com.denver7074.bot.service.response.SendMsg;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;
import java.util.List;

import static java.util.Collections.emptyList;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InvoiceService {

    BotConfig botConfig;
    ObjectMapper objectMapper;
    FileMessageService fileMessageService;
    @NonFinal
    @Value("${yandex.kit.url}")
    String POST_URL_SPEECH;

    // распознание голоса работает, пока отправляет обратно,
    // но можно будет потом дописать код по распределению 
    public SendMessage handleUpdateVoice(Update update) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(botConfig.getYandexKey());
        byte[] voice = fileMessageService.loadFile(update.getMessage().getVoice().getFileId());
        HttpEntity<byte[]> requestEntity = new HttpEntity<>(voice, headers);

        ResponseEntity<JsonNode> responseEntity = new RestTemplate().exchange(POST_URL_SPEECH + botConfig.getFolderId(), HttpMethod.POST, requestEntity, JsonNode.class);
        return new SendMsg(
                responseEntity.getBody().elements().next().asText(),
                update.getMessage().getFrom().getId(),
                emptyList()).getMsg();
    }
}
