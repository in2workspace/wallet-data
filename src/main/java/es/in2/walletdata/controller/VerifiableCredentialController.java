package es.in2.walletdata.controller;

import es.in2.walletdata.domain.CredentialRequest;
import es.in2.walletdata.domain.VCTypeList;
import es.in2.walletdata.domain.VcBasicData;
import es.in2.walletdata.facade.UserDataFacadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

import static es.in2.walletdata.utils.Utils.getUserIdFromToken;

@RestController
@RequestMapping("/api/v2/credentials")
@Slf4j
@RequiredArgsConstructor
public class VerifiableCredentialController {

    private final UserDataFacadeService userDataFacadeService;

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
    public Mono<Void> saveVerifiableCredential(@RequestBody CredentialRequest credentialRequest, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        log.debug("VerifiableCredentialController.saveVerifiableCredential");
        return getUserIdFromToken(authorizationHeader)
                .flatMap(userId -> userDataFacadeService.saveVerifiableCredentialByUserId(userId, credentialRequest.credential()))
                .then();
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
    public Mono<List<VcBasicData>> getSelectableVCs(@RequestBody VCTypeList vcTypeList, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        log.debug("VerifiableCredentialController.getSelectableVCs()");
        return getUserIdFromToken(authorizationHeader)
                .flatMap(userId -> userDataFacadeService.getVCsByVcTypeList(userId, vcTypeList.vcTypes()));
    }

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
    public Mono<List<VcBasicData>> getVerifiableCredentialList(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        log.debug("VerifiableCredentialController.getVerifiableCredential()");
        return getUserIdFromToken(authorizationHeader)
                .flatMap(userDataFacadeService::getUserVCs);
    }

    @GetMapping("/id")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get Verifiable Credential by id",
            description = "Get the Verifiable Credential that matches with the specified id",
            tags = {"Verifiable Credential Management"}
    )
    @ApiResponse(responseCode = "200", description = "Verifiable credential retrieved successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid request.")
    @ApiResponse(responseCode = "404", description = "Verifiable credential don't match with the specified id")
    @ApiResponse(responseCode = "500", description = "Internal server error.")
    public Mono<String> getSelectableVCs(@RequestParam String credentialId, @RequestParam String format, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        log.debug("VerifiableCredentialController.getSelectableVCs()");
        return getUserIdFromToken(authorizationHeader)
                .flatMap(userId -> userDataFacadeService.getVerifiableCredentialByIdAndFormat(userId, credentialId, format));
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
    public Mono<Void> deleteVerifiableCredential(@RequestParam String credentialId, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        log.debug("VerifiableCredentialController.deleteVerifiableCredential()");
        return getUserIdFromToken(authorizationHeader)
                .flatMap(userId -> userDataFacadeService.deleteVerifiableCredentialById(credentialId, userId))
                .then();
    }

}
