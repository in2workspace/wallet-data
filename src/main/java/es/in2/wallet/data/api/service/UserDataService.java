package es.in2.wallet.data.api.service;

import es.in2.wallet.data.api.domain.*;
import es.in2.wallet.data.api.util.DidMethods;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserDataService {

    Mono<UserEntity> saveVC(UserEntity userEntity, String vcJwt);
    Mono<List<VcBasicDataDTO>> getUserVCsInJson(UserEntity userEntity);
    Mono<List<VCAttribute>> getVerifiableCredentialsByFormat(UserEntity userEntity,String format);
    Mono<String> getVerifiableCredentialByIdAndFormat(UserEntity userEntity, String id, String format);
    Mono<String> extractDidFromVerifiableCredential(UserEntity userEntity, String vcId);
    Mono<UserEntity> deleteVerifiableCredential(UserEntity userEntity, String vcId, String did);
    Mono<List<VcBasicDataDTO>> getSelectableVCsByVcTypeList(List<String> vcTypeList, UserEntity userEntity);
    Mono<UserEntity> saveDid(UserEntity userEntity, String did, DidMethods didMethod);
    Mono<List<String>> getDidsByUserEntity(UserEntity userEntity);
    Mono<UserEntity> deleteSelectedDidFromUserEntity(String did, UserEntity userEntity);
    Mono<UserAttribute> getUserDataFromUserEntity(UserEntity userEntity);
    Mono<UserEntity> createUserEntity(UserRequestDTO userRequestDTO);

}
