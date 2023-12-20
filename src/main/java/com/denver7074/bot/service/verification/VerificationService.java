package com.denver7074.bot.service.verification;

import com.denver7074.bot.model.Equipment;
import com.denver7074.bot.model.Subscriber;
import com.denver7074.bot.service.CrudService;
import com.denver7074.bot.utils.RedisCash;
import com.denver7074.bot.utils.errors.ToThrow;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.denver7074.bot.utils.Constants.SORT;
import static com.denver7074.bot.utils.errors.Errors.E002;
import static com.denver7074.bot.utils.errors.Errors.E004;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class VerificationService {

    @NonFinal
    @Value("${fgis.gost.service.url}")
    String uri;
    @NonFinal
    @Value("${verification.openfeign}")
    Boolean openfeign;
    ObjectMapper mapper;
    RedisCash redisCash;
    CrudService crudService;
    Verification verification;


    public Equipment findLastVerification(Long userId, String mitnumber, String number) {
        List<Equipment> list = new ArrayList<>();
        if (openfeign) list.addAll(findLastVerificationWithFeignClient(mitnumber, number));
        else list.addAll(findLastVerification(mitnumber, number));
        Equipment eq = list.stream().findFirst().orElse(null);
        if (isNotEmpty(eq)) eq.setUserId(userId);
        return eq;
    }

    @ToThrow
    public Equipment findLastVerification(Subscriber user, String command) {
        Equipment equipment = findLastVerification(command).stream().findFirst().orElse(null);
        E002.thr(isEmpty(equipment), user.getId(), command);
        List<Equipment> equipments = crudService.find(Equipment.class, Map.of(
                Equipment.Fields.userId, user.getId(),
                Equipment.Fields.mitNumber, equipment.getMitNumber(),
                Equipment.Fields.number, equipment.getNumber()
        ), null);
        E004.thr(isNotEmpty(equipments), user.getId(), equipment);
        redisCash.save(user, equipment);
        return equipment;
    }

    public List<Equipment> findLastVerification(String command) {
        String[] s = command.trim().split(" ");
        if (openfeign) return findLastVerificationWithFeignClient(s[0], s[1]);
        return findLastVerification(s[0], s[1]);
    }

//    @SneakyThrows
//    private List<Equipment> findLastVerification(String mitnumber, String number) {
//        String POST_API_URI = uri + String.format("/select?fq=mi.mitnumber:%s&fq=mi.number:%s",mitnumber, number)
//                + String.format("&q=*&sort=%s&rows=15&start=0", String.join(",", SORT));
//        HttpClient client = HttpClient.newHttpClient();
//        HttpRequest request = HttpRequest.newBuilder()
//                .GET()
//                .header("accept", "application/json")
//                .uri(URI.create(POST_API_URI))
//                .build();
//        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//        JsonNode json = mapper.readTree(response.body());
//        JsonNode docsArray = json.get("response").get("docs");
//        CollectionLikeType postCollection = mapper.getTypeFactory().constructCollectionLikeType(ArrayList.class, Equipment.class);
//        return mapper.convertValue(docsArray, postCollection);
//    }

    public List<Equipment> findLastVerification(String mitnumber, String number) {
        String postApiUri = uri + "/select";

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(postApiUri)
                .queryParam("fq", "mi.mitnumber:" + mitnumber)
                .queryParam("fq", "mi.number:" + number)
                .queryParam("q", "*")
                .queryParam("sort", String.join(",", SORT))
                .queryParam("rows", 15)
                .queryParam("start", 0);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        RequestEntity<?> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, URI.create(builder.toUriString()));

        ResponseEntity<JsonNode> responseEntity = new RestTemplate().exchange(
                requestEntity,
                JsonNode.class
        );
        JsonNode docsArray = responseEntity.getBody().path("response").path("docs");
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
