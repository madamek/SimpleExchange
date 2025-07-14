package com.example.simpleexchange.nbp.config;

import org.springframework.context.annotation.*;

public class NbpClientConfiguration {

    @Bean
    public NbpErrorDecoder nbpErrorDecoder() {
        return new NbpErrorDecoder();
    }

}