package es.in2.wallet.data.api.controller;

import es.in2.wallet.data.api.model.UserAttribute;
import es.in2.wallet.data.api.model.UserRequestDTO;
import es.in2.wallet.data.api.service.UserDataFacadeService;
import es.in2.wallet.data.api.utils.ApplicationUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserDataFacadeService userDataFacadeService;

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
        return userDataFacadeService.createUserEntity(appUserRequestDTO);
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
    public Mono<UserAttribute> getUserDataByUserId(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader){
        log.debug("UserController.getUserDataByUserId");
        log.debug("VerifiableCredentialController.getSelectableVCs()");
        return ApplicationUtils.getUserIdFromToken(authorizationHeader)
                .flatMap(userDataFacadeService::getUserDataByUserId);
    }
}