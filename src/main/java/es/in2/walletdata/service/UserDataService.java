package es.in2.walletdata.service;

import es.in2.walletdata.domain.*;
import es.in2.walletdata.domain.DidMethods;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserDataService {

    Mono<UserEntity> saveVC(UserEntity userEntity, String vcJwt);

    Mono<List<VcBasicData>> getUserVCsInJson(UserEntity userEntity);

    Mono<List<VCAttribute>> getVerifiableCredentialsByFormat(UserEntity userEntity, String format);

    Mono<String> getVerifiableCredentialByIdAndFormat(UserEntity userEntity, String id, String format);

    Mono<String> extractDidFromVerifiableCredential(UserEntity userEntity, String vcId);

    Mono<UserEntity> deleteVerifiableCredential(UserEntity userEntity, String vcId, String did);

    Mono<List<VcBasicData>> getSelectableVCsByVcTypeList(List<String> vcTypeList, UserEntity userEntity);

    Mono<UserEntity> saveDid(UserEntity userEntity, String did, DidMethods didMethod);

    Mono<List<String>> getDidsByUserEntity(UserEntity userEntity);

    Mono<UserEntity> deleteSelectedDidFromUserEntity(String did, UserEntity userEntity);

    Mono<UserAttribute> getUserDataFromUserEntity(UserEntity userEntity);

    Mono<UserEntity> createUserEntity(UserRegistrationRequestEvent userRegistrationRequestEvent);

}
