package com.denver7074.bot.service.messageservice.invoice;

import com.denver7074.bot.configuration.BotConfig;
import com.denver7074.bot.service.messageservice.FileMessageService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvoiceService {

    final BotConfig botConfig;
    final FileMessageService fileMessageService;
    @Value("${yandex.kit.url}")
    String POST_URL_SPEECH;


    @SneakyThrows
    public String handleUpdateVoice(Update update) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(POST_URL_SPEECH))
                .header("Authorization", botConfig.getYandexKey())
                .POST(HttpRequest.BodyPublishers.ofByteArray(fileMessageService.loadFile(update)))
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> post = client.send(request, HttpResponse.BodyHandlers.ofString());
        return post.body().substring(post.body().indexOf(":") + 2,post.body().length()-2);
    }

//    доработать
    public void handleUpdateVoiceRest(Update update) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(botConfig.getYandexKey());
        RequestEntity<?> requestEntity = new RequestEntity<>(headers, HttpMethod.POST, URI.create(POST_URL_SPEECH));

        ResponseEntity<JsonNode> responseEntity = new RestTemplate().exchange(
                requestEntity,
                JsonNode.class
        );
    }

//    public String handleUpdateVoiceFeign(Update update) {
//        String data = invoiceRep.getData(API_KEY_YANDEX_CLOUD, fileMessageService.loadFile(update));
//        return data;
//    }
}
