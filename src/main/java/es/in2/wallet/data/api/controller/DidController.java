package es.in2.wallet.data.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.SignedJWT;
import es.in2.wallet.data.api.model.DidRequestDTO;
import es.in2.wallet.data.api.service.OrionLDService;
import es.in2.wallet.data.api.utils.DidMethods;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.text.ParseException;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import reactor.core.publisher.Mono;
import static es.in2.wallet.data.api.utils.ApiUtils.*;

@RestController
@RequestMapping("/api/dids")
@RequiredArgsConstructor
public class DidController {

    private final OrionLDService orionLDService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Save DID",
            description = "Save a Decentralized Identifier (DID)",
            tags = {"DID Management"}
    )
    @ApiResponse(responseCode = "201", description = "DID created successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid request.")
    @ApiResponse(responseCode = "500", description = "Internal server error.")

    public Mono<Void> saveDid(@RequestBody DidRequestDTO didRequestDTO,@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) throws JsonProcessingException, ParseException {
        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER)) {
            String token = authorizationHeader.substring(7);
            SignedJWT parsedVcJwt = SignedJWT.parse(token);
            JsonNode jsonObject = new ObjectMapper().readTree(parsedVcJwt.getPayload().toString());
            String userId = jsonObject.get("sub").asText();
            return orionLDService.saveDid(didRequestDTO.getDid(), DidMethods.fromStringValue(didRequestDTO.getDidType()), userId);
        } else {
            return Mono.error(new IllegalArgumentException(INVALID_AUTH_HEADER));
        }
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get list of DIDs",
            description = "Retrieve a list of Decentralized Identifier (DID) objects associated with the current user.",
            tags = {"DID Management"}
    )
    @ApiResponse(responseCode = "200", description = "List of DIDs retrieved successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid request.")
    @ApiResponse(responseCode = "500", description = "Internal server error.")
    public Mono<List<String>> getDidListByUserId(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) throws JsonProcessingException, ParseException {
        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER)) {
            String token = authorizationHeader.substring(7);
            SignedJWT parsedVcJwt = SignedJWT.parse(token);
            JsonNode jsonObject = new ObjectMapper().readTree(parsedVcJwt.getPayload().toString());
            String userId = jsonObject.get("sub").asText();
            return orionLDService.getDidsByUserId(userId);
        } else {
            return Mono.error(new IllegalArgumentException(INVALID_AUTH_HEADER));
        }
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Delete DID",
            description = "Delete the specified Decentralized Identifier (DID) object from the personal data space.",
            tags = {"DID Management"}
    )
    @ApiResponse(responseCode = "200", description = "DID deleted successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid request.")
    @ApiResponse(responseCode = "404", description = "Did not found")
    @ApiResponse(responseCode = "500", description = "Internal server error.")

    public Mono<Void> deleteDid(@RequestParam String did,@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) throws JsonProcessingException, ParseException {
        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER)) {
            String token = authorizationHeader.substring(7);
            SignedJWT parsedVcJwt = SignedJWT.parse(token);
            JsonNode jsonObject = new ObjectMapper().readTree(parsedVcJwt.getPayload().toString());
            String userId = jsonObject.get("sub").asText();
            return orionLDService.deleteSelectedDid(did,userId);
        } else {
            return Mono.error(new IllegalArgumentException(INVALID_AUTH_HEADER));
        }
    }
}

