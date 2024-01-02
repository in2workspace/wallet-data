package es.in2.walletdata.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import es.in2.walletdata.configuration.properties.BrokerProperties;
import es.in2.walletdata.domain.UserEntity;
import es.in2.walletdata.exception.FailedCommunicationException;
import es.in2.walletdata.exception.NoSuchUserEntity;
import es.in2.walletdata.exception.ParseErrorException;
import es.in2.walletdata.service.impl.BrokerServiceImpl;
import es.in2.walletdata.utils.Utils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static es.in2.walletdata.utils.Utils.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BrokerServiceImplTest {
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private BrokerProperties brokerProperties;
    @InjectMocks
    private BrokerServiceImpl brokerService;

    @Test
    void storeUserInContextBrokerTest() throws JsonProcessingException {
        try (MockedStatic<Utils> ignored = Mockito.mockStatic(Utils.class)){
            List<Map.Entry<String, String>> headers = new ArrayList<>();
            headers.add(new AbstractMap.SimpleEntry<>(CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON));
            UserEntity mockUserEntity = UserEntity.builder().id("123").build();

            when(brokerProperties.domain()).thenReturn("https://example.com");
            when(brokerProperties.url()).thenReturn("/url");
            ObjectWriter mockWriter = mock(ObjectWriter.class);
            when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(mockWriter);
            when(mockWriter.writeValueAsString(mockUserEntity)).thenReturn("user entity");

            when(postRequest("https://example.com","/url",headers,"user entity")).thenReturn(Mono.just("entity saved"));

            StepVerifier.create(brokerService.storeUserInContextBroker(mockUserEntity))
                    .verifyComplete();
        }
    }
    @Test
    void storeUserInContextBrokerFailedCommunicationErrorTest() throws JsonProcessingException {
        try (MockedStatic<Utils> ignored = Mockito.mockStatic(Utils.class)){
            List<Map.Entry<String, String>> headers = new ArrayList<>();
            headers.add(new AbstractMap.SimpleEntry<>(CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON));
            UserEntity mockUserEntity = UserEntity.builder().id("123").build();

            when(brokerProperties.domain()).thenReturn("https://example.com");
            when(brokerProperties.url()).thenReturn("/url");
            ObjectWriter mockWriter = mock(ObjectWriter.class);
            when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(mockWriter);
            when(mockWriter.writeValueAsString(mockUserEntity)).thenReturn("user entity");

            when(postRequest("https://example.com","/url",headers,"user entity"))
                    .thenReturn(Mono.error(new RuntimeException("Communication error")));

            StepVerifier.create(brokerService.storeUserInContextBroker(mockUserEntity))
                    .expectError(FailedCommunicationException.class)
                    .verify();
        }
    }
    @Test
    void getUserEntityFromContextBrokerTest() throws JsonProcessingException {
        try (MockedStatic<Utils> ignored = Mockito.mockStatic(Utils.class)){
            List<Map.Entry<String, String>> headers = new ArrayList<>();
            UserEntity mockUserEntity = UserEntity.builder().id("123").build();
            String userId = "123";

            when(brokerProperties.domain()).thenReturn("https://example.com");
            when(brokerProperties.url()).thenReturn("/url");
            when(objectMapper.readValue(anyString(), eq(UserEntity.class))).thenReturn(mockUserEntity);

            when(getRequest("https://example.com","/url/urn:entities:userId:123",headers)).thenReturn(Mono.just("user entity"));


            StepVerifier.create(brokerService.getUserEntityFromContextBroker(userId))
                    .expectNext(mockUserEntity)
                    .verifyComplete();
        }
    }

    @Test
    void getUserEntityFromContextBrokerNoSuchUserEntityTest(){
        try (MockedStatic<Utils> ignored = Mockito.mockStatic(Utils.class)){
            List<Map.Entry<String, String>> headers = new ArrayList<>();
            String userId = "123";
            when(brokerProperties.domain()).thenReturn("https://example.com");
            when(brokerProperties.url()).thenReturn("/url");

            when(getRequest("https://example.com","/url/urn:entities:userId:123",headers)).thenReturn(Mono.error(new RuntimeException("Error retrieving UserEntity for userId: " + userId)));

            StepVerifier.create(brokerService.getUserEntityFromContextBroker(userId))
                    .expectError(NoSuchUserEntity.class)
                    .verify();
        }
    }

    @Test
    void storeUserInContextBrokerParseErrorExceptionTest() throws JsonProcessingException {
        try (MockedStatic<Utils> ignored = Mockito.mockStatic(Utils.class)){
            List<Map.Entry<String, String>> headers = new ArrayList<>();
            String userId = "123";

            when(brokerProperties.domain()).thenReturn("https://example.com");
            when(brokerProperties.url()).thenReturn("/url");
            when(objectMapper.readValue(anyString(), eq(UserEntity.class))).thenThrow(new JsonProcessingException("Deserialization error") {});

            when(getRequest("https://example.com","/url/urn:entities:userId:123",headers)).thenReturn(Mono.just("user entity"));


            StepVerifier.create(brokerService.getUserEntityFromContextBroker(userId))
                    .expectError(ParseErrorException.class)
                    .verify();
        }
    }
    @Test
    void updateUserEntityInContextBrokerTest() throws JsonProcessingException {
        try (MockedStatic<Utils> ignored = Mockito.mockStatic(Utils.class)){
            List<Map.Entry<String, String>> headers = new ArrayList<>();
            headers.add(new AbstractMap.SimpleEntry<>(CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON));
            UserEntity mockUserEntity = UserEntity.builder().id("123").build();
            String userId = "123";

            when(brokerProperties.domain()).thenReturn("https://example.com");
            when(brokerProperties.url()).thenReturn("/url");
            ObjectWriter mockWriter = mock(ObjectWriter.class);
            when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(mockWriter);
            when(mockWriter.writeValueAsString(mockUserEntity)).thenReturn("user entity");

            when(patchRequest("https://example.com","/url/urn:entities:userId:123/attrs",headers,"user entity")).thenReturn(Mono.just("entity saved"));

            StepVerifier.create(brokerService.updateUserEntityInContextBroker(mockUserEntity,userId))
                    .verifyComplete();
        }
    }
    @Test
    void updateUserEntityInContextBrokerFailedCommunicationExceptionTest() throws JsonProcessingException {
        try (MockedStatic<Utils> ignored = Mockito.mockStatic(Utils.class)){
            List<Map.Entry<String, String>> headers = new ArrayList<>();
            headers.add(new AbstractMap.SimpleEntry<>(CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON));
            UserEntity mockUserEntity = UserEntity.builder().id("123").build();
            String userId = "123";

            when(brokerProperties.domain()).thenReturn("https://example.com");
            when(brokerProperties.url()).thenReturn("/url");
            ObjectWriter mockWriter = mock(ObjectWriter.class);
            when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(mockWriter);
            when(mockWriter.writeValueAsString(mockUserEntity)).thenReturn("user entity");

            when(patchRequest("https://example.com","/url/urn:entities:userId:123/attrs",headers,"user entity")).thenReturn(Mono.error(new RuntimeException("Error while updating user entity: " + userId)));

            StepVerifier.create(brokerService.updateUserEntityInContextBroker(mockUserEntity,userId))
                    .expectError(FailedCommunicationException.class)
                    .verify();
        }
    }

    @Test
    void updateUserEntityInContextBrokerParseErrorExceptionTest() throws JsonProcessingException {
        try (MockedStatic<Utils> ignored = Mockito.mockStatic(Utils.class)){
            UserEntity mockUserEntity = UserEntity.builder().id("123").build();
            String userId = "123";
            
            when(brokerProperties.url()).thenReturn("/url");
            ObjectWriter mockWriter = mock(ObjectWriter.class);
            when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(mockWriter);
            when(mockWriter.writeValueAsString(mockUserEntity)).thenThrow(new JsonProcessingException("Serialization error") {});

            StepVerifier.create(brokerService.updateUserEntityInContextBroker(mockUserEntity,userId))
                    .expectError(ParseErrorException.class)
                    .verify();
        }
    }


}

