package com.example.simpleexchange.exception;

import com.example.simpleexchange.domain.Currency;
import com.example.simpleexchange.dto.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.exc.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;
import org.springframework.http.*;
import org.springframework.http.converter.*;
import org.springframework.security.access.*;
import org.springframework.validation.*;
import org.springframework.web.bind.*;
import org.springframework.web.method.annotation.*;

import java.math.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    private BindingResult bindingResult;

    @Nested
    @DisplayName("Tests for handleRestException")
    class HandleRestExceptionTests {

        @Test
        @DisplayName("should return correct error response for RestException")
        void handleRestException_shouldReturnCorrectErrorResponse() {
            // given
            RestException ex = new ResourceNotFoundException("Test resource not found");

            // when
            ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleRestException(ex);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("Test resource not found");
            assertThat(response.getBody().status()).isEqualTo(HttpStatus.NOT_FOUND.value());
            assertThat(response.getBody().error()).isEqualTo(HttpStatus.NOT_FOUND.getReasonPhrase());
        }
    }

    @Nested
    @DisplayName("Tests for handleValidationExceptions")
    class HandleValidationExceptionsTests {

        @BeforeEach
        void setUp() {
            when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        }

        @Test
        @DisplayName("should return BAD_REQUEST for MethodArgumentNotValidException")
        void handleValidationExceptions_shouldReturnBadRequest() {
            // given
            FieldError fieldError = new FieldError("objectName", "fieldName", "default message");
            when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

            // when
            ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleValidationExceptions(methodArgumentNotValidException);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("{fieldName=default message}");
            assertThat(response.getBody().status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(response.getBody().error()).isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }

        @Test
        @DisplayName("should return BAD_REQUEST with empty errors for MethodArgumentNotValidException with no field errors")
        void handleValidationExceptions_whenNoFieldErrors_shouldReturnBadRequestWithEmptyErrors() {
            // given
            when(bindingResult.getFieldErrors()).thenReturn(Collections.emptyList());

            // when
            ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleValidationExceptions(methodArgumentNotValidException);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("{}");
            assertThat(response.getBody().status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(response.getBody().error()).isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
    }

    @Nested
    @DisplayName("Tests for handleMessageNotReadable")
    class HandleMessageNotReadableTests {

        @Test
        @DisplayName("should return BAD_REQUEST for HttpMessageNotReadableException")
        void handleMessageNotReadable_shouldReturnBadRequest() {
            // given
            HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Malformed JSON");

            // when
            ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleMessageNotReadable(ex);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("Request body is malformed");
            assertThat(response.getBody().status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(response.getBody().error()).isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }

        @Test
        @DisplayName("should return BAD_REQUEST with specific message for InvalidFormatException on enum")
        void handleMessageNotReadable_whenInvalidFormatExceptionOnEnum_shouldReturnSpecificMessage() {
            // given
            InvalidFormatException invalidFormatException = new InvalidFormatException(null, "", "invalid", Currency.class);
            invalidFormatException.prependPath(new JsonMappingException.Reference(new Object(), "currency"));
            HttpMessageNotReadableException ex = new HttpMessageNotReadableException("", invalidFormatException);

            // when
            ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleMessageNotReadable(ex);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("Invalid value 'invalid' for field 'currency'. Allowed values are: [PLN, USD]");
            assertThat(response.getBody().status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(response.getBody().error()).isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
    }

    @Nested
    @DisplayName("Tests for handleTypeMismatch")
    class HandleTypeMismatchTests {

        @Test
        @DisplayName("should return BAD_REQUEST for MethodArgumentTypeMismatchException")
        void handleTypeMismatch_shouldReturnBadRequest() {
            // given
            MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException("invalid", BigDecimal.class, "paramName", null, null);

            // when
            ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleTypeMismatch(ex);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("Parameter 'paramName' is not valid. Value 'invalid' is not a valid 'BigDecimal'.");
            assertThat(response.getBody().status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(response.getBody().error()).isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
    }

    @Nested
    @DisplayName("Tests for handleGenericException")
    class HandleGenericExceptionTests {

        @Test
        @DisplayName("should return INTERNAL_SERVER_ERROR for generic Exception")
        void handleGenericException_shouldReturnInternalServerError() {
            // given
            Exception ex = new RuntimeException("Something went wrong");

            // when
            ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGenericException(ex);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("An unexpected internal server error occurred.");
            assertThat(response.getBody().status()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
            assertThat(response.getBody().error()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }

    @Nested
    @DisplayName("Tests for handleUserAlreadyExists")
    class HandleUserAlreadyExistsTests {

        @Test
        @DisplayName("should return CONFLICT for UserAlreadyExistsException")
        void handleUserAlreadyExists_shouldReturnConflict() {
            // given
            UserAlreadyExistsException ex = new UserAlreadyExistsException("User exists");

            // when
            ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleUserAlreadyExists(ex);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("User exists");
            assertThat(response.getBody().status()).isEqualTo(HttpStatus.CONFLICT.value());
            assertThat(response.getBody().error()).isEqualTo(HttpStatus.CONFLICT.getReasonPhrase());
        }
    }

    @Nested
    @DisplayName("Tests for handleAccessDenied")
    class HandleAccessDeniedTests {

        @Test
        @DisplayName("should return FORBIDDEN for AccessDeniedException")
        void handleAccessDenied_shouldReturnForbidden() {
            // given
            AccessDeniedException ex = new AccessDeniedException("Access denied");

            // when
            ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleAccessDenied(ex);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("Access Denied. You do not have permission to access this resource.");
            assertThat(response.getBody().status()).isEqualTo(HttpStatus.FORBIDDEN.value());
            assertThat(response.getBody().error()).isEqualTo(HttpStatus.FORBIDDEN.getReasonPhrase());
        }
    }

    @Nested
    @DisplayName("Tests for handleExchangeRateProviderException")
    class HandleExchangeRateProviderExceptionTests {

        @Test
        @DisplayName("should return SERVICE_UNAVAILABLE for ExchangeRateProviderException")
        void handleExchangeRateProviderException_shouldReturnServiceUnavailable() {
            // given
            Throwable cause = new RuntimeException("Root cause");
            ExchangeRateProviderException ex = new ExchangeRateProviderException("NBP API unavailable", cause);

            // when
            ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleExchangeRateProviderException(ex);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("NBP API unavailable: Root cause");
            assertThat(response.getBody().status()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE.value());
            assertThat(response.getBody().error()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase());
        }
    }

    @Nested
    @DisplayName("Tests for handleInvalidCurrencyException")
    class HandleInvalidCurrencyExceptionTests {

        @Test
        @DisplayName("should return BAD_REQUEST for InvalidCurrencyException")
        void handleInvalidCurrencyException_shouldReturnBadRequest() {
            // given
            InvalidCurrencyException ex = new InvalidCurrencyException("Invalid currency");

            // when
            ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleInvalidCurrencyException(ex);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("Invalid currency");
            assertThat(response.getBody().status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(response.getBody().error()).isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
    }
}
