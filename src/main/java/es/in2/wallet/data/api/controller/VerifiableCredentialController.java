package es.in2.wallet.data.api.controller;

import es.in2.wallet.data.api.model.CredentialRequestDTO;
import es.in2.wallet.data.api.model.VCTypeListDTO;
import es.in2.wallet.data.api.model.VcBasicDataDTO;
import es.in2.wallet.data.api.service.OrionLDService;
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
    public Mono<List<VcBasicDataDTO>> getVerifiableCredentialList(@RequestParam String userId){
        log.debug("VerifiableCredentialController.getVerifiableCredential()");
        return orionLDService.getUserVCsInJson(userId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> deleteVerifiableCredential(@RequestParam String credentialId,@RequestParam String userId){
        log.debug("VerifiableCredentialController.deleteVerifiableCredential()");
        return orionLDService.deleteVerifiableCredential(credentialId,userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> saveVerifiableCredential(@RequestBody CredentialRequestDTO credentialRequestDTO){
        log.debug("VerifiableCredentialController.saveVerifiableCredential()");
        return orionLDService.saveVC(credentialRequestDTO.getCredential(), credentialRequestDTO.getUserId());
    }
    @PostMapping("/types")
    @ResponseStatus(HttpStatus.OK)
    public Mono<List<VcBasicDataDTO>> getSelectableVCs(@RequestBody VCTypeListDTO vcTypeListDTO){
        log.debug("VerifiableCredentialController.getVerifiableCredential()");
        return orionLDService.getSelectableVCsByVcTypeList(vcTypeListDTO.getVcTypes(), vcTypeListDTO.getUserId());
    }
}