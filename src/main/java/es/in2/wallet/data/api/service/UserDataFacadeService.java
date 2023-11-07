package es.in2.wallet.data.api.service;

import es.in2.wallet.data.api.model.UserAttribute;
import es.in2.wallet.data.api.model.UserRequestDTO;
import es.in2.wallet.data.api.model.VcBasicDataDTO;
import es.in2.wallet.data.api.utils.DidMethods;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserDataFacadeService {
    Mono<Void> saveVerifiableCredentialByUserId(String userId, String vcJwt);
    Mono<List<VcBasicDataDTO>> getUserVCs(String userId);
    Mono<Void> deleteVerifiableCredentialById(String credentialId,String userId);
    Mono<List<VcBasicDataDTO>> getVCsByVcTypeList(String userId, List<String> vcTypeList);
    Mono<Void> saveDidByUserId(String userId, String did, DidMethods didMethod);
    Mono<Void> deleteDid(String userId,String did);
    Mono<List<String>> getDidsByUserId(String userId);
    Mono<Void> createUserEntity(UserRequestDTO userRequestDTO);
    Mono<UserAttribute> getUserDataByUserId(String userId);
    Mono<String> getVerifiableCredentialByIdAndFormat(String userId, String credentialId, String format);
}
