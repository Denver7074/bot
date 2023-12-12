package com.denver7074.bot.service.verification;

import com.denver7074.bot.model.Equipment;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static com.denver7074.bot.utils.Constants.SORT;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class VerificationService {

    @NonFinal
    @Value("${fgis.gost.service.url}")
    String uri;
    @NonFinal
    @Value("${verification.openfeign}")
    Boolean openfeign;
    ObjectMapper mapper;
    Verification verification;

    public VerificationService(ObjectMapper mapper, Verification verification) {
        this.mapper = mapper;
        this.verification = verification;
    }

    public List<Equipment> findLastVerification(String command) {
        String[] s = command.trim().split(" ");
        if (openfeign) return findLastVerificationWithFeignClient(s[0], s[1]);
        return findLastVerification(s[0], s[1]);
    }

    @SneakyThrows
    private List<Equipment> findLastVerification(String mitnumber, String number) {
        String POST_API_URI = uri + String.format("/select?fq=mi.mitnumber:%s&fq=mi.number:%s",mitnumber, number)
                + String.format("&q=*&sort=%s&rows=15&start=0", String.join(",", SORT));
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .header("accept", "application/json")
                .uri(URI.create(POST_API_URI))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode json = mapper.readTree(response.body());
        JsonNode docsArray = json.get("response").get("docs");
        CollectionLikeType postCollection = mapper.getTypeFactory().constructCollectionLikeType(ArrayList.class, Equipment.class);
        return mapper.convertValue(docsArray, postCollection);
    }

    //не работает
    @SneakyThrows
    private List<Equipment> findLastVerificationWithFeignClient(String mitnumber, String number) {
        String data = verification.getData(
                new String[]{"mi.mitnumber:21730-13"},
                "*",
                String.join(",", SORT),
                10,
                0
        );
        JsonNode json = mapper.readTree(data);
        JsonNode docsArray = json.get("response").get("docs");
        CollectionLikeType postCollection = mapper.getTypeFactory().constructCollectionLikeType(ArrayList.class, Equipment.class);
        return mapper.convertValue(docsArray, postCollection);
    }
}