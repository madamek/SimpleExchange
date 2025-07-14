package com.example.simpleexchange.nbp;

import com.example.simpleexchange.nbp.config.*;
import com.example.simpleexchange.nbp.dto.*;
import org.springframework.cloud.openfeign.*;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "nbp-api",
        url = "${clients.nbp.url}",
        configuration = NbpClientConfiguration.class)
public interface NbpRestClient {

    @GetMapping("/exchangerates/rates/c/{currencyCode}/?format=json")
    NbpExchangeRateResponse getRatesForCurrency(@PathVariable("currencyCode") String currencyCode);

}