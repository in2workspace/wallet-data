package es.in2.wallet.data.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.SignedJWT;
import es.in2.wallet.data.api.model.CredentialRequestDTO;
import es.in2.wallet.data.api.model.VCTypeListDTO;
import es.in2.wallet.data.api.model.VcBasicDataDTO;
import es.in2.wallet.data.api.service.OrionLDService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.text.ParseException;
import java.util.List;

import static es.in2.wallet.data.api.utils.ApiUtils.*;

@RestController
@RequestMapping("/api/credentials")
@Slf4j
@RequiredArgsConstructor
public class VerifiableCredentialController {

    private final OrionLDService orionLDService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "List Verifiable Credentials",
            description = "Retrieve a list of Verifiable Credentials",
            tags = {"Verifiable Credential Management"}
    )
    @ApiResponse(responseCode = "200", description = "Verifiable credential retrieved successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid request.")
    @ApiResponse(responseCode = "500", description = "Internal server error.")
    public Mono<List<VcBasicDataDTO>> getVerifiableCredentialList(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) throws JsonProcessingException, ParseException {
        log.debug("VerifiableCredentialController.getVerifiableCredential()");
        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER)) {
            String token = authorizationHeader.substring(7);
            SignedJWT parsedVcJwt = SignedJWT.parse(token);
            JsonNode jsonObject = new ObjectMapper().readTree(parsedVcJwt.getPayload().toString());
            String userId = jsonObject.get("sub").asText();
            return orionLDService.getUserVCsInJson(userId);
        } else {
            return Mono.error(new IllegalArgumentException(INVALID_AUTH_HEADER));
        }
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Delete Verifiable Credential",
            description = "Delete the verifiable credential from the context broker.",
            tags = {"Verifiable Credential Management"}
    )
    @ApiResponse(responseCode = "200", description = "Verifiable credential deleted successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid request.")
    @ApiResponse(responseCode = "404", description = "Verifiable credential not found")
    @ApiResponse(responseCode = "500", description = "Internal server error.")
    public Mono<Void> deleteVerifiableCredential(@RequestParam String credentialId,@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) throws JsonProcessingException, ParseException {
        log.debug("VerifiableCredentialController.deleteVerifiableCredential()");
        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER)) {
            String token = authorizationHeader.substring(7);
            SignedJWT parsedVcJwt = SignedJWT.parse(token);
            JsonNode jsonObject = new ObjectMapper().readTree(parsedVcJwt.getPayload().toString());
            String userId = jsonObject.get("sub").asText();
            return orionLDService.deleteVerifiableCredential(credentialId,userId);
        } else {
            return Mono.error(new IllegalArgumentException(INVALID_AUTH_HEADER));
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Save Verifiable Credential",
            description = "Save a verifiable credential",
            tags = {"Verifiable Credential Management"}
    )
    @ApiResponse(responseCode = "201", description = "Verifiable credential saved successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid request.")
    @ApiResponse(responseCode = "500", description = "Internal server error.")
    public Mono<Void> saveVerifiableCredential(@RequestBody CredentialRequestDTO credentialRequestDTO,@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) throws JsonProcessingException, ParseException {
        log.debug("VerifiableCredentialController.saveVerifiableCredential()");
        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER)) {
            String token = authorizationHeader.substring(7);
            SignedJWT parsedVcJwt = SignedJWT.parse(token);
            JsonNode jsonObject = new ObjectMapper().readTree(parsedVcJwt.getPayload().toString());
            String userId = jsonObject.get("sub").asText();
            return orionLDService.saveVC(credentialRequestDTO.getCredential(), userId);
        } else {
            return Mono.error(new IllegalArgumentException(INVALID_AUTH_HEADER));
        }
    }
    @PostMapping("/types")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "List Verifiable Credentials by type",
            description = "Retrieve a list of Verifiable Credentials that matches with the specified types",
            tags = {"Verifiable Credential Management"}
    )
    @ApiResponse(responseCode = "200", description = "Verifiable credential retrieved successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid request.")
    @ApiResponse(responseCode = "404", description = "Verifiable credential don't match with the specified types")
    @ApiResponse(responseCode = "500", description = "Internal server error.")
    public Mono<List<VcBasicDataDTO>> getSelectableVCs(@RequestBody VCTypeListDTO vcTypeListDTO, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) throws ParseException, JsonProcessingException {
        log.debug("VerifiableCredentialController.getVerifiableCredential()");
        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER)) {
            String token = authorizationHeader.substring(7);
            SignedJWT parsedVcJwt = SignedJWT.parse(token);
            JsonNode jsonObject = new ObjectMapper().readTree(parsedVcJwt.getPayload().toString());
            String userId = jsonObject.get("sub").asText();
            return orionLDService.getSelectableVCsByVcTypeList(vcTypeListDTO.getVcTypes(), userId);

        } else {
            return Mono.error(new IllegalArgumentException(INVALID_AUTH_HEADER));
        }
    }
}