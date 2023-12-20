package com.denver7074.bot.service.messageservice.invoice;

import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Repository
@FeignClient(name = "invoice", url = "https://stt.api.cloud.yandex.net/speech/v1/stt:recognize")
public interface InvoiceRep {


    @PostMapping
    String getData(@RequestHeader("Authorization") String apiKey, @RequestBody byte[] requestBody);
}
