package es.in2.wallet.data.api.controller;

import es.in2.wallet.data.api.model.UserAttribute;
import es.in2.wallet.data.api.model.UserRequestDTO;
import es.in2.wallet.data.api.service.OrionLDService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final OrionLDService orionLDService;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    public UserController(OrionLDService orionLDService) {
        this.orionLDService = orionLDService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Void> registerUser(@RequestBody @Valid UserRequestDTO appUserRequestDTO){
        log.debug("UserController.registerUser()");
        return orionLDService.registerUserInContextBroker(appUserRequestDTO);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Mono<UserAttribute> getUserDataByUserId(@RequestParam String userId){
        log.debug("UserController.getUserDataByUserId");
        return orionLDService.getUserDataByUserId(userId);
    }
}