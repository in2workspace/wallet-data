package es.in2.walletdata.api.facade;

import es.in2.walletdata.domain.*;
import es.in2.walletdata.facade.impl.UserDataFacadeServiceImpl;
import es.in2.walletdata.service.BrokerService;
import es.in2.walletdata.service.UserDataService;
import es.in2.walletdata.service.WalletCryptoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.AbstractMap;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDataFacadeServiceImplTest {
    @Mock
    private UserDataService userDataService;
    @Mock
    private BrokerService brokerService;
    @Mock
    private WalletCryptoService walletCryptoService;
    @InjectMocks
    private UserDataFacadeServiceImpl userDataFacadeService;

    @Test
    void saveVerifiableCredentialByUserIdTest(){
        String userId = "123";
        UserEntity userEntity = UserEntity.builder().id("123").build();
        String vc = "credential";

        when(brokerService.getUserEntityFromContextBroker(userId)).thenReturn(Mono.just(userEntity));
        when(userDataService.saveVC(userEntity, vc)).thenReturn(Mono.just(userEntity));
        when(brokerService.updateUserEntityInContextBroker(userEntity,userId)).thenReturn(Mono.empty());

        StepVerifier.create(userDataFacadeService.saveVerifiableCredentialByUserId(userId,vc))
                .verifyComplete();

    }
    @Test
    void deleteVerifiableCredentialByIdTest(){
        String userId = "123";
        UserEntity userEntity = UserEntity.builder().id("123").build();
        String did = "did:key:123";
        String vcId = "credential123";

        when(brokerService.getUserEntityFromContextBroker(userId)).thenReturn(Mono.just(userEntity));
        when(userDataService.extractDidFromVerifiableCredential(userEntity, vcId)).thenReturn(Mono.just(did));
        when(walletCryptoService.deletePrivateKeyAssociateToDID(did)).thenReturn(Mono.just(did));
        when(userDataService.deleteVerifiableCredential(userEntity,vcId,did)).thenReturn(Mono.just(userEntity));
        when(brokerService.updateUserEntityInContextBroker(userEntity,userId)).thenReturn(Mono.empty());

        StepVerifier.create(userDataFacadeService.deleteVerifiableCredentialById(vcId,userId))
                .verifyComplete();

    }

    @Test
    void getUserVCsTest(){
        String userId = "123";
        UserEntity userEntity = UserEntity.builder().id("123").build();
        List<VcBasicData> vcs = List.of(VcBasicData.builder().id("123").build());

        when(brokerService.getUserEntityFromContextBroker(userId)).thenReturn(Mono.just(userEntity));
        when(userDataService.getUserVCsInJson(userEntity)).thenReturn(Mono.just(vcs));

        StepVerifier.create(userDataFacadeService.getUserVCs(userId))
                .expectNext(vcs)
                .verifyComplete();

    }

    @Test
    void getVCsByVcTypeListTest(){
        String userId = "123";
        UserEntity userEntity = UserEntity.builder().id("123").build();
        List<String> vcTypeList = List.of("LEARCredential");
        List<VcBasicData> vcs = List.of(VcBasicData.builder().id("123").build());

        when(brokerService.getUserEntityFromContextBroker(userId)).thenReturn(Mono.just(userEntity));
        when(userDataService.getSelectableVCsByVcTypeList(vcTypeList,userEntity)).thenReturn(Mono.just(vcs));

        StepVerifier.create(userDataFacadeService.getVCsByVcTypeList(userId,vcTypeList))
                .expectNext(vcs)
                .verifyComplete();

    }

    @Test
    void saveDidByUserIdTest(){
        String userId = "123";
        UserEntity userEntity = UserEntity.builder().id("123").build();
        String did = "did:key:123";
        when(brokerService.getUserEntityFromContextBroker(userId)).thenReturn(Mono.just(userEntity));
        when(userDataService.saveDid(userEntity, did, DidMethods.KEY)).thenReturn(Mono.just(userEntity));
        when(brokerService.updateUserEntityInContextBroker(userEntity,userId)).thenReturn(Mono.empty());

        StepVerifier.create(userDataFacadeService.saveDidByUserId(userId,did,DidMethods.KEY))
                .verifyComplete();

    }

    @Test
    void getDidsByUserIdTest(){
        String userId = "123";
        UserEntity userEntity = UserEntity.builder().id("123").build();
        List<String> dids = List.of("did:key:123");
        when(brokerService.getUserEntityFromContextBroker(userId)).thenReturn(Mono.just(userEntity));
        when(userDataService.getDidsByUserEntity(userEntity)).thenReturn(Mono.just(dids));

        StepVerifier.create(userDataFacadeService.getDidsByUserId(userId))
                .expectNext(dids)
                .verifyComplete();

    }

    @Test
    void createUserEntityTest(){
        UserRequest userRequest = UserRequest.builder().userId("1234").build();

        when(userDataService.createUserEntity(userRequest)).thenReturn(Mono.empty());

        StepVerifier.create(userDataFacadeService.createUserEntity(userRequest))
                .verifyComplete();

    }

    @Test
    void getUserDataByUserIdTest(){
        String userId = "123";
        UserEntity userEntity = UserEntity.builder().id("123").build();
        UserAttribute userAttribute = UserAttribute.builder().username("Jhon Doe").build();
        when(brokerService.getUserEntityFromContextBroker(userId)).thenReturn(Mono.just(userEntity));
        when(userDataService.getUserDataFromUserEntity(userEntity)).thenReturn(Mono.just(userAttribute));

        StepVerifier.create(userDataFacadeService.getUserDataByUserId(userId))
                .expectNext(userAttribute)
                .verifyComplete();

    }
    @Test
    void getVerifiableCredentialByIdAndFormatTest(){
        String userId = "123";
        String credentialId = "321";
        String format = "vc_json";
        UserEntity userEntity = UserEntity.builder().id("123").build();
        String vc = "credential";

        when(brokerService.getUserEntityFromContextBroker(userId)).thenReturn(Mono.just(userEntity));
        when(userDataService.getVerifiableCredentialByIdAndFormat(userEntity,credentialId,format)).thenReturn(Mono.just(vc));

        StepVerifier.create(userDataFacadeService.getVerifiableCredentialByIdAndFormat(userId,credentialId,format))
                .expectNext(vc)
                .verifyComplete();

    }


}
