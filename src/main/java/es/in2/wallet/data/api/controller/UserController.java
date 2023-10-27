package es.in2.wallet.data.api.controller;

import es.in2.wallet.data.api.model.UserAttribute;
import es.in2.wallet.data.api.model.UserRequestDTO;
import es.in2.wallet.data.api.service.OrionLDService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    @Operation(
            summary = "User Register",
            description = "User registered correctly",
            tags = {"User Management"}
    )
    @ApiResponse(responseCode = "201", description = "User created successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid request.")
    @ApiResponse(responseCode = "500", description = "Internal server error.")
    public Mono<Void> registerUser(@RequestBody @Valid UserRequestDTO appUserRequestDTO){
        log.debug("UserController.registerUser()");
        return orionLDService.registerUserInContextBroker(appUserRequestDTO);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "User Data",
            description = "User Data retrieved",
            tags = {"User Management"}
    )
    @ApiResponse(responseCode = "200", description = "User Data retrieved successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid request.")
    @ApiResponse(responseCode = "404", description = "UserId don't match with any users on context broker.")
    @ApiResponse(responseCode = "500", description = "Internal server error.")
    public Mono<UserAttribute> getUserDataByUserId(@RequestParam String userId){
        log.debug("UserController.getUserDataByUserId");
        return orionLDService.getUserDataByUserId(userId);
    }
}