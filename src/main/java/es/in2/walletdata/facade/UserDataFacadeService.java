package es.in2.walletdata.facade;

import es.in2.walletdata.domain.UserAttribute;
import es.in2.walletdata.domain.UserRequest;
import es.in2.walletdata.domain.VcBasicData;
import es.in2.walletdata.utils.DidMethods;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserDataFacadeService {
    Mono<Void> saveVerifiableCredentialByUserId(String userId, String vcJwt);

    Mono<List<VcBasicData>> getUserVCs(String userId);

    Mono<Void> deleteVerifiableCredentialById(String credentialId, String userId);

    Mono<List<VcBasicData>> getVCsByVcTypeList(String userId, List<String> vcTypeList);

    Mono<Void> saveDidByUserId(String userId, String did, DidMethods didMethod);

    Mono<List<String>> getDidsByUserId(String userId);

    Mono<Void> createUserEntity(UserRequest userRequest);

    Mono<UserAttribute> getUserDataByUserId(String userId);

    Mono<String> getVerifiableCredentialByIdAndFormat(String userId, String credentialId, String format);
}
