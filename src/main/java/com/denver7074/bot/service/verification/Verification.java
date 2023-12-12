package com.denver7074.bot.service.verification;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "verification", url = "https://fgis.gost.ru/fundmetrology/cm/xcdb/vri")
public interface Verification {

    @GetMapping("/select")
    String getData(
            @RequestParam("fq") String[] fqValues,
            @RequestParam("q") String q,
            @RequestParam("sort") String sort,
            @RequestParam("rows") int rows,
            @RequestParam("start") int start
    );

}
