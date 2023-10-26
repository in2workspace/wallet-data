package es.in2.wallet.data.api.controller;

import es.in2.wallet.data.api.model.DidRequestDTO;
import es.in2.wallet.data.api.service.OrionLDService;
import es.in2.wallet.data.api.utils.DidMethods;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

@Tag(name = "DID Management", description = "Personal Data Space for DID Management API")
@RestController
@RequestMapping("/api/dids")
public class DidController {

    private final OrionLDService orionLDService;

    public DidController(OrionLDService orionLDService) {
        this.orionLDService = orionLDService;
    }

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

    public Mono<Void> saveDid(@RequestBody DidRequestDTO didRequestDTO){
        return orionLDService.saveDid(didRequestDTO.getDid(), DidMethods.valueOf(didRequestDTO.getDidType()), didRequestDTO.getUserId());
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get list of DIDs",
            description = "Retrieve a list of Decentralized Identifier (DID) objects associated with the current user.",
            tags = {"DID Management"}
    )
    @ApiResponse(responseCode = "200", description = "List of DIDs retrieved successfully.")
    @ApiResponse(responseCode = "500", description = "Internal server error.")
    public Mono<List<String>> getDidListByUserId(@RequestParam String userId){
        return orionLDService.getDidsByUserId(userId);
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
    @ApiResponse(responseCode = "500", description = "Internal server error.")

    public Mono<Void> deleteDid(@RequestParam String did,@RequestParam String userId){
        return orionLDService.deleteSelectedDid(did,userId);
    }
}

