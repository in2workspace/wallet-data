package es.in2.wallet.data.api.service;

import es.in2.wallet.data.api.model.UserAttribute;
import es.in2.wallet.data.api.model.UserRequestDTO;
import es.in2.wallet.data.api.model.VCAttribute;
import es.in2.wallet.data.api.model.VcBasicDataDTO;
import es.in2.wallet.data.api.utils.DidMethods;
import reactor.core.publisher.Mono;

import java.util.List;

public interface OrionLDService {

    Mono<Void> saveVC(String vcJwt,String userId);
    Mono<List<VcBasicDataDTO>> getUserVCsInJson(String userId);
    Mono<List<VCAttribute>> getVerifiableCredentialsByUserIdAndFormat(String format, String userId);
    Mono<String> getVerifiableCredentialByIdAndFormat(String id, String format, String userId);
    Mono<Void> deleteVerifiableCredential(String credentialId, String userId);
    Mono<List<VcBasicDataDTO>> getSelectableVCsByVcTypeList(List<String> vcTypeList,String userId);
    Mono<Void> saveDid(String did, DidMethods didMethod, String userId);
    Mono<List<String>> getDidsByUserId(String userId);
    Mono<Void> deleteSelectedDid(String did,String userId);
    Mono<UserAttribute> getUserDataByUserId(String userId);
    Mono<Void> registerUserInContextBroker(UserRequestDTO userRequestDTO);

}
