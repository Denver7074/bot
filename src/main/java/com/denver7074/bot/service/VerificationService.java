package com.denver7074.bot.service;

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
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static com.denver7074.bot.utils.Constants.SORT;
import static com.denver7074.bot.utils.errors.Errors.E002;
import static com.denver7074.bot.utils.errors.Errors.E004;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class VerificationService {

    @NonFinal
    @Value("${fgis.gost.service.url}")
    String uri;
    ObjectMapper mapper;
    RedisCash redisCash;
    CrudService crudService;

    public Equipment findLastVerification(Long userId, String mitnumber, String number) {
        List<Equipment> list = findLastVerification(mitnumber, number);
        Equipment eq = list.stream().findFirst().orElse(null);
        if (isNotEmpty(eq)) eq.setUserId(userId);
        return eq;
    }

    @ToThrow
    public Equipment findLastVerification(Subscriber user, String command) {
        Equipment equipment = findLastVerification(command).stream().findFirst().orElse(null);
        E002.thr(isEmpty(equipment), user.getId(), emptyList(), command);
        List<Equipment> equipments = crudService.find(Equipment.class, Map.of(
                Equipment.Fields.userId, user.getId(),
                Equipment.Fields.mitNumber, equipment.getMitNumber(),
                Equipment.Fields.number, equipment.getNumber()
        ), null);
        E004.thr(isNotEmpty(equipments), user.getId(), emptyList(), equipment);
        redisCash.save(user, equipment);
        return equipment;
    }

    public List<Equipment> findLastVerification(String command) {
        String[] s = command.trim().split(" ");
        return findLastVerification(s[0], s[1]);
    }

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
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        RequestEntity<?> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, URI.create(builder.toUriString()));

        ResponseEntity<JsonNode> responseEntity = new RestTemplate().exchange(
                requestEntity,
                JsonNode.class
        );
        JsonNode docsArray = responseEntity.getBody().path("response").path("docs");
        CollectionLikeType postCollection = mapper.getTypeFactory().constructCollectionLikeType(List.class, Equipment.class);
        return mapper.convertValue(docsArray, postCollection);
    }
}
