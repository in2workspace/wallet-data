package es.in2.walletdata.controller;

import es.in2.walletdata.domain.DidMethods;
import es.in2.walletdata.domain.DidRequest;
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

@Slf4j
@RestController
@RequestMapping("/api/v2/dids")
@RequiredArgsConstructor
public class DidController {

    private final UserDataFacadeService userDataFacadeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Save DID", description = "Save a Decentralized Identifier (DID)", tags = {"DID Management"})
    @ApiResponse(responseCode = "201", description = "DID created successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid request.")
    @ApiResponse(responseCode = "500", description = "Internal server error.")

    public Mono<Void> saveDid(@RequestBody DidRequest didRequest, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        log.debug("DidController.saveDid()");
        return getUserIdFromToken(authorizationHeader).flatMap(userId -> userDataFacadeService.saveDidByUserId(userId, didRequest.did(), DidMethods.fromStringValue(didRequest.didType()))).then();
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get list of DIDs", description = "Retrieve a list of Decentralized Identifier (DID) objects associated with the current user.", tags = {"DID Management"})
    @ApiResponse(responseCode = "200", description = "List of DIDs retrieved successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid request.")
    @ApiResponse(responseCode = "500", description = "Internal server error.")
    public Mono<List<String>> getDidListByUserId(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        log.debug("DidController.getDidListByUserId");
        return getUserIdFromToken(authorizationHeader).flatMap(userDataFacadeService::getDidsByUserId);
    }

}
