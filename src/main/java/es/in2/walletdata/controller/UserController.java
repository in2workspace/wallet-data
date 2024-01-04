package es.in2.walletdata.controller;

import es.in2.walletdata.domain.UserAttribute;
import es.in2.walletdata.domain.UserRequest;
import es.in2.walletdata.facade.UserDataFacadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static es.in2.walletdata.utils.Utils.getUserIdFromToken;

@RestController
@RequestMapping("/api/v2/users")
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
    public Mono<Void> registerUser(@RequestBody @Valid UserRequest appUserRequest) {
        log.debug("UserController.registerUser()");
        return userDataFacadeService.createUserEntity(appUserRequest);
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
    public Mono<UserAttribute> getUserDataByUserId(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        log.debug("UserController.getUserDataByUserId");
        log.debug("VerifiableCredentialController.getSelectableVCs()");
        return getUserIdFromToken(authorizationHeader)
                .flatMap(userDataFacadeService::getUserDataByUserId);
    }

}