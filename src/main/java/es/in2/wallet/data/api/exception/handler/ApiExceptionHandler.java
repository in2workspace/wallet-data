package es.in2.wallet.data.api.exception.handler;
import es.in2.wallet.data.api.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.util.List;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(FailedCommunicationException.class)
    public Mono<ResponseEntity<Void>> failedCommunicationException(FailedCommunicationException e) {
        log.error(e.getMessage());
        return Mono.just(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }
    @ExceptionHandler(NoSuchVerifiableCredentialException.class)
    public Mono<ResponseEntity<Void>> noSuchVerifiableCredentialException(NoSuchVerifiableCredentialException e) {
        log.error(e.getMessage());
        return Mono.just(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    @ExceptionHandler(NoSuchDidException.class)
    public Mono<ResponseEntity<Void>> noSuchDidException(NoSuchDidException e) {
        log.error(e.getMessage());
        return Mono.just(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    @ExceptionHandler(NoSuchUserEntity.class)
    public Mono<ResponseEntity<Void>> noSuchUserEntityException(NoSuchUserEntity e) {
        log.error(e.getMessage());
        return Mono.just(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    @ExceptionHandler(ParseErrorException.class)
    public Mono<ResponseEntity<Void>> parseErrorException(ParseErrorException e) {
        log.error(e.getMessage());
        return Mono.just(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }
    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ResponseEntity<List<String>>> handleValidationExceptions(WebExchangeBindException ex) {
        List<String> errors = ex.getBindingResult().getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .toList();
        log.error("Validation error: {}", errors);
        return Mono.just(ResponseEntity.badRequest().body(errors));
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<Void>> unknownDidMethod(IllegalArgumentException e) {
        log.error(e.getMessage());
        return Mono.just(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }
}