package com.example.simpleexchange.nbp.config;

import com.example.simpleexchange.exception.*;
import feign.*;
import feign.codec.*;
import org.springframework.stereotype.*;

@Component
public class NbpErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == 404) {
            return new ResourceNotFoundException("Currency not found in NBP API.");
        }

        Exception exception = defaultErrorDecoder.decode(methodKey, response);

        if (exception instanceof RetryableException) {
            return exception;
        }

        return new ExchangeRateProviderException(
                "Error during communication with NBP API: " + exception.getMessage(), exception
        );
    }
}
