package es.in2.wallet.data.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.SignedJWT;
import es.in2.wallet.data.api.model.UserAttribute;
import es.in2.wallet.data.api.model.UserRequestDTO;
import es.in2.wallet.data.api.service.OrionLDService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.text.ParseException;

import static es.in2.wallet.data.api.utils.ApiUtils.BEARER;
import static es.in2.wallet.data.api.utils.ApiUtils.INVALID_AUTH_HEADER;

@RestController
@RequestMapping("/api/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final OrionLDService orionLDService;

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
    public Mono<UserAttribute> getUserDataByUserId(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) throws JsonProcessingException, ParseException {
        log.debug("UserController.getUserDataByUserId");
        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER)) {
            String token = authorizationHeader.substring(7);
            SignedJWT parsedVcJwt = SignedJWT.parse(token);
            JsonNode jsonObject = new ObjectMapper().readTree(parsedVcJwt.getPayload().toString());
            String userId = jsonObject.get("sub").asText();
            return orionLDService.getUserDataByUserId(userId);
        } else {
            return Mono.error(new IllegalArgumentException(INVALID_AUTH_HEADER));
        }
    }
}