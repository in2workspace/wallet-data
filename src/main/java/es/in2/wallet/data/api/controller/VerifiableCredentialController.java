package es.in2.wallet.data.api.controller;

import es.in2.wallet.data.api.model.CredentialRequestDTO;
import es.in2.wallet.data.api.model.VCTypeListDTO;
import es.in2.wallet.data.api.model.VcBasicDataDTO;
import es.in2.wallet.data.api.service.OrionLDService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/credentials")
public class VerifiableCredentialController {

    private final OrionLDService orionLDService;
    private static final Logger log = LoggerFactory.getLogger(VerifiableCredentialController.class);

    public VerifiableCredentialController(OrionLDService orionLDService) {
        this.orionLDService = orionLDService;
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
    public Mono<List<VcBasicDataDTO>> getVerifiableCredentialList(@RequestParam String userId){
        log.debug("VerifiableCredentialController.getVerifiableCredential()");
        return orionLDService.getUserVCsInJson(userId);
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
    public Mono<Void> deleteVerifiableCredential(@RequestParam String credentialId,@RequestParam String userId){
        log.debug("VerifiableCredentialController.deleteVerifiableCredential()");
        return orionLDService.deleteVerifiableCredential(credentialId,userId);
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
    public Mono<Void> saveVerifiableCredential(@RequestBody CredentialRequestDTO credentialRequestDTO){
        log.debug("VerifiableCredentialController.saveVerifiableCredential()");
        return orionLDService.saveVC(credentialRequestDTO.getCredential(), credentialRequestDTO.getUserId());
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
    public Mono<List<VcBasicDataDTO>> getSelectableVCs(@RequestBody VCTypeListDTO vcTypeListDTO){
        log.debug("VerifiableCredentialController.getVerifiableCredential()");
        return orionLDService.getSelectableVCsByVcTypeList(vcTypeListDTO.getVcTypes(), vcTypeListDTO.getUserId());
    }
}