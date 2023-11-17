package es.in2.wallet.data.api.controller;

import es.in2.wallet.data.api.model.DidRequestDTO;
import es.in2.wallet.data.api.service.UserDataFacadeService;
import es.in2.wallet.data.api.utils.ApplicationUtils;
import es.in2.wallet.data.api.utils.DidMethods;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/dids")
@Slf4j
@RequiredArgsConstructor
public class DidController {

    private final UserDataFacadeService userDataFacadeService;


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

    public Mono<Void> saveDid(@RequestBody DidRequestDTO didRequestDTO,@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader){
        log.debug("DidController.saveDid()");
        return ApplicationUtils.getUserIdFromToken(authorizationHeader)
                .flatMap(userId -> userDataFacadeService.saveDidByUserId(userId, didRequestDTO.getDid(), DidMethods.fromStringValue(didRequestDTO.getDidType())))
                .then();
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
    public Mono<List<String>> getDidListByUserId(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader){
        log.debug("DidController.getDidListByUserId");
        return ApplicationUtils.getUserIdFromToken(authorizationHeader)
                .flatMap(userDataFacadeService::getDidsByUserId);
    }

//    @DeleteMapping
//    @ResponseStatus(HttpStatus.OK)
//    @Operation(
//            summary = "Delete DID",
//            description = "Delete the specified Decentralized Identifier (DID) object from the personal data space.",
//            tags = {"DID Management"}
//    )
//    @ApiResponse(responseCode = "200", description = "DID deleted successfully.")
//    @ApiResponse(responseCode = "400", description = "Invalid request.")
//    @ApiResponse(responseCode = "404", description = "Did not found")
//    @ApiResponse(responseCode = "500", description = "Internal server error.")
//
//    public Mono<Void> deleteDid(@RequestParam String did,@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader){
//        log.debug("DidController.deleteDid");
//        return ApplicationUtils.getUserIdFromToken(authorizationHeader)
//                .flatMap(userId -> userDataFacadeService.deleteDid(userId, did))
//                .then();
//    }
}

