package es.in2.wallet.data.api.service;

import es.in2.wallet.data.api.model.UserAttribute;
import es.in2.wallet.data.api.model.UserRequestDTO;
import es.in2.wallet.data.api.model.VcBasicDataDTO;
import es.in2.wallet.data.api.service.impl.OrionLDServiceImpl;
import es.in2.wallet.data.api.utils.ApplicationUtils;
import es.in2.wallet.data.api.utils.DidMethods;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@WebFluxTest(OrionLDServiceImpl.class)
@Import({ApplicationUtils.class})
class OrionLDServiceImplTest {

    @Autowired
    private OrionLDServiceImpl orionLDService;

    @MockBean
    private ApplicationUtils applicationUtils;

    @Value("${app.url.orion-ld-adapter}")
    private String contextBrokerEntitiesURL;
    

    @Test
    void testSaveVC() {
        // Sample JWT token for a verifiable credential
        String vcJwt = "eyJraWQiOiJkaWQ6a2V5OnpRM3NodGNFUVAzeXV4YmtaMVNqTjUxVDhmUW1SeVhuanJYbThFODRXTFhLRFFiUm4jelEzc2h0Y0VRUDN5dXhia1oxU2pONTFUOGZRbVJ5WG5qclhtOEU4NFdMWEtEUWJSbiIsInR5cCI6IkpXVCIsImFsZyI6IkVTMjU2SyJ9.eyJzdWIiOiJkaWQ6a2V5OnpEbmFlZnk3amhwY0ZCanp0TXJFSktFVHdFU0NoUXd4cEpuVUpLb3ZzWUQ1ZkpabXAiLCJuYmYiOjE2OTgxMzQ4NTUsImlzcyI6ImRpZDprZXk6elEzc2h0Y0VRUDN5dXhia1oxU2pONTFUOGZRbVJ5WG5qclhtOEU4NFdMWEtEUWJSbiIsImV4cCI6MTcwMDcyNjg1NSwiaWF0IjoxNjk4MTM0ODU1LCJ2YyI6eyJ0eXBlIjpbIlZlcmlmaWFibGVDcmVkZW50aWFsIiwiTEVBUkNyZWRlbnRpYWwiXSwiQGNvbnRleHQiOlsiaHR0cHM6Ly93d3cudzMub3JnLzIwMTgvY3JlZGVudGlhbHMvdjEiLCJodHRwczovL2RvbWUtbWFya2V0cGxhY2UuZXUvLzIwMjIvY3JlZGVudGlhbHMvbGVhcmNyZWRlbnRpYWwvdjEiXSwiaWQiOiJ1cm46dXVpZDo4NzAwYmVlNS00NjIxLTQ3MjAtOTRkZS1lODY2ZmI3MTk3ZTkiLCJpc3N1ZXIiOnsiaWQiOiJkaWQ6a2V5OnpRM3NodGNFUVAzeXV4YmtaMVNqTjUxVDhmUW1SeVhuanJYbThFODRXTFhLRFFiUm4ifSwiaXNzdWFuY2VEYXRlIjoiMjAyMy0xMC0yNFQwODowNzozNVoiLCJpc3N1ZWQiOiIyMDIzLTEwLTI0VDA4OjA3OjM1WiIsInZhbGlkRnJvbSI6IjIwMjMtMTAtMjRUMDg6MDc6MzVaIiwiZXhwaXJhdGlvbkRhdGUiOiIyMDIzLTExLTIzVDA4OjA3OjM1WiIsImNyZWRlbnRpYWxTdWJqZWN0Ijp7ImlkIjoiZGlkOmtleTp6RG5hZWZ5N2pocGNGQmp6dE1yRUpLRVR3RVNDaFF3eHBKblVKS292c1lENWZKWm1wIiwidGl0bGUiOiJNci4iLCJmaXJzdF9uYW1lIjoiSm9obiIsImxhc3RfbmFtZSI6IkRvZSIsImdlbmRlciI6Ik0iLCJwb3N0YWxfYWRkcmVzcyI6IiIsImVtYWlsIjoiam9obmRvZUBnb29kYWlyLmNvbSIsInRlbGVwaG9uZSI6IiIsImZheCI6IiIsIm1vYmlsZV9waG9uZSI6IiszNDc4NzQyNjYyMyIsImxlZ2FsUmVwcmVzZW50YXRpdmUiOnsiY24iOiI1NjU2NTY1NlYgSmVzdXMgUnVpeiIsInNlcmlhbE51bWJlciI6IjU2NTY1NjU2ViIsIm9yZ2FuaXphdGlvbklkZW50aWZpZXIiOiJWQVRFUy0xMjM0NTY3OCIsIm8iOiJHb29kQWlyIiwiYyI6IkVTIn0sInJvbGVzQW5kRHV0aWVzIjpbeyJ0eXBlIjoiTEVBUkNyZWRlbnRpYWwiLCJpZCI6Imh0dHBzOi8vZG9tZS1tYXJrZXRwbGFjZS5ldS8vbGVhci92MS82NDg0OTk0bjRyOWU5OTA0OTQifV0sImtleSI6InZhbHVlIn19LCJqdGkiOiJ1cm46dXVpZDo4NzAwYmVlNS00NjIxLTQ3MjAtOTRkZS1lODY2ZmI3MTk3ZTkifQ.2_YNY515CaohirD4AHDBMvzDagEn-p8uAsaiMT0H4ltK2uVfG8IWWqV_OOR6lFlXMzUhJd7nKsaWkhnAQY8kyA";
        String userId = "1234";

        // Sample JSON response that the applicationUtils.getRequest() method will be mocked to return
        String jsonResponse = """
                { "id": "urn:entities:userId:1234", "type": "userEntity", "dids": {
                            "type": "Property",
                            "value": []
                        },
                        "userData": {
                            "type": "Property",
                            "value": {
                                "username": "Manuel",
                                "email": "manuel@gmail.com"
                            }
                        },
                        "vcs": {
                            "type": "Property",
                            "value": []
                        }}""";


        // When applicationUtils.getRequest() is called with any String and any List, return a Mono with the jsonResponse
        Mockito.when(applicationUtils.getRequest(Mockito.anyString(), Mockito.anyList()))
                .thenReturn(Mono.just(jsonResponse));

        // When applicationUtils.patchRequest() is called with any String, any List, and any String, return an empty Mono
        Mockito.when(applicationUtils.patchRequest(Mockito.anyString(), Mockito.anyList(), Mockito.anyString()))
                .thenReturn(Mono.empty());

        // Executing the method under test
        StepVerifier.create(orionLDService.saveVC(vcJwt, userId))
                .expectComplete()
                .verify();

        // Verifying the interactions with the mocks and ensuring that the expected behaviors occurred
        Mockito.verify(applicationUtils).getRequest(Mockito.eq(contextBrokerEntitiesURL + "/api/v1/entities/urn:entities:userId:" + userId), Mockito.anyList());
        Mockito.verify(applicationUtils).patchRequest(Mockito.eq(contextBrokerEntitiesURL + "/api/v1/update"), Mockito.anyList(), Mockito.anyString());

    }
    @Test
    void testGetUserVCsInJson(){
        String userId = "1234";
        // Sample user entity JSON response
        String userEntityJson = """
            {
                "id": "urn:entities:userId:1234",
                "type": "userEntity",
                "dids": {
                    "type": "Property",
                    "value": []
                },
                "userData": {
                    "type": "Property",
                    "value": {
                        "username": "John Doe",
                        "email": "john@gmail.com"
                    }
                },
                "vcs": {
                    "type": "Property",
                    "value": [
                        {
                            "id": "vc1",
                            "type": "vc_json",
                            "value": {
                                "type": ["VerifiableCredential", "SpecificCredentialType"],
                                "credentialSubject": {
                                    "name": "John Doe"
                                }
                            }
                        },
                        {
                            "id": "vc2",
                            "type": "vc_json",
                            "value": {
                                "type": ["VerifiableCredential", "LEARCredential"],
                                "credentialSubject": {
                                    "name": "John Doe",
                                    "age": "25"
                                }
                            }
                        }
                        
                    ]
                }
            }
        """;
        Mockito.when(applicationUtils.getRequest(Mockito.anyString(), Mockito.anyList()))
                .thenReturn(Mono.just(userEntityJson));
        // Executing the method under test
        StepVerifier.create(orionLDService.getUserVCsInJson(userId))
                .assertNext(vcBasicDataDTOList -> {
                    assertFalse(vcBasicDataDTOList.isEmpty(), "The VC list should not be empty");

                    assertEquals("vc1", vcBasicDataDTOList.get(0).getId());
                    assertTrue(vcBasicDataDTOList.get(0).getVcType().contains("SpecificCredentialType"));

                    assertEquals("vc2", vcBasicDataDTOList.get(1).getId());
                    assertTrue(vcBasicDataDTOList.get(1).getVcType().contains("LEARCredential"));
                })
                .expectComplete()
                .verify();

        // Verifying interactions
        Mockito.verify(applicationUtils).getRequest(Mockito.eq(contextBrokerEntitiesURL + "/api/v1/entities/urn:entities:userId:" + userId), Mockito.anyList());
    }
    @Test
    void testGetSelectableVCsByVcTypeList() {
        // Arrange
        String userId = "1234";
        List<String> vcTypeList = Arrays.asList("VerifiableCredential", "LEARCredential");
        String userEntityJson = """
            {
                "id": "urn:entities:userId:1234",
                "type": "userEntity",
                "dids": {
                    "type": "Property",
                    "value": []
                },
                "userData": {
                    "type": "Property",
                    "value": {
                        "username": "John Doe",
                        "email": "john@gmail.com"
                    }
                },
                "vcs": {
                    "type": "Property",
                    "value": [
                        {
                            "id": "123",
                            "type": "vc_json",
                            "value": {
                                "type": ["VerifiableCredential", "SpecificCredentialType"],
                                "credentialSubject": {
                                    "name": "John Doe"
                                },
                                "id": "123"
                            }
                        },
                        {
                            "id": "1234",
                            "type": "vc_json",
                            "value": {
                                "type": ["VerifiableCredential", "LEARCredential"],
                                "credentialSubject": {
                                    "name": "John Doe",
                                    "age": "25"
                                },
                                "id": "1234"
                            }
                        }
                        
                    ]
                }
            }
        """;
        Mockito.when(applicationUtils.getRequest(Mockito.anyString(), Mockito.anyList()))
                .thenReturn(Mono.just(userEntityJson));
        // Act and Assert
        StepVerifier.create(orionLDService.getSelectableVCsByVcTypeList(vcTypeList, userId))
                .assertNext(vcBasicDataDTOList -> {
                    // Assertions for vcBasicDataDTOList
                    // Adjust according to your expectations
                    assertFalse(vcBasicDataDTOList.isEmpty(), "The list of VCs should not be empty");
                    VcBasicDataDTO firstVc = vcBasicDataDTOList.get(0);
                    assertEquals("1234", firstVc.getId());
                    assertTrue(firstVc.getVcType().containsAll(vcTypeList));
                })
                .expectComplete()
                .verify();

        // Verify interactions
        Mockito.verify(applicationUtils).getRequest(Mockito.eq(contextBrokerEntitiesURL + "/api/v1/entities/urn:entities:userId:" + userId), Mockito.anyList());
    }
    @Test
    void tesDeleteVC() {
        // Sample JWT token for a verifiable credential
        String vcID = "vc2";
        String userId = "1234";

        // Sample JSON response that the applicationUtils.getRequest() method will be mocked to return
        String userEntityJson = """
            {
                "id": "urn:entities:userId:1234",
                "type": "userEntity",
                "dids": {
                    "type": "Property",
                    "value": []
                },
                "userData": {
                    "type": "Property",
                    "value": {
                        "username": "John Doe",
                        "email": "john@gmail.com"
                    }
                },
                "vcs": {
                    "type": "Property",
                    "value": [
                        {
                            "id": "vc1",
                            "type": "vc_json",
                            "value": {
                                "type": ["VerifiableCredential", "SpecificCredentialType"],
                                "credentialSubject": {
                                    "name": "John Doe"
                                },
                                "id": "vc1"
                            }
                        },
                        {
                            "id": "vc2",
                            "type": "vc_json",
                            "value": {
                                "type": ["VerifiableCredential", "LEARCredential"],
                                "credentialSubject": {
                                    "name": "John Doe",
                                    "age": "25"
                                },
                                "id": "vc2"
                            }
                        }
                        
                    ]
                }
            }
        """;


        // When applicationUtils.getRequest() is called with any String and any List, return a Mono with the jsonResponse
        Mockito.when(applicationUtils.getRequest(Mockito.anyString(), Mockito.anyList()))
                .thenReturn(Mono.just(userEntityJson));

        // When applicationUtils.patchRequest() is called with any String, any List, and any String, return an empty Mono
        Mockito.when(applicationUtils.patchRequest(Mockito.anyString(), Mockito.anyList(), Mockito.anyString()))
                .thenReturn(Mono.empty());

        // Executing the method under test
        StepVerifier.create(orionLDService.deleteVerifiableCredential(vcID,userId))
                .expectComplete()
                .verify();

        // Verifying the interactions with the mocks and ensuring that the expected behaviors occurred
        Mockito.verify(applicationUtils).getRequest(Mockito.eq(contextBrokerEntitiesURL + "/api/v1/entities/urn:entities:userId:" + userId), Mockito.anyList());
        Mockito.verify(applicationUtils).patchRequest(Mockito.eq(contextBrokerEntitiesURL + "/api/v1/update"), Mockito.anyList(), Mockito.anyString());
    }
    @Test
    void testSaveDid() {
        // Sample JWT token for a verifiable credential
        String did = "did:key:1234";
        String didType = "KEY";
        String userId = "1234";

        // Sample JSON response that the applicationUtils.getRequest() method will be mocked to return
        String jsonResponse = """
                {
                    "id": "urn:entities:userId:1234",
                    "type": "userEntity",
                    "dids": {
                        "type": "Property",
                        "value": []
                    },
                    "userData": {
                        "type": "Property",
                        "value": {
                            "username": "Manuel",
                            "email": "manuel@gmail.com"
                        }
                    },
                    "vcs": {
                        "type": "Property",
                        "value": []
                    }
                }""";


        // When applicationUtils.getRequest() is called with any String and any List, return a Mono with the jsonResponse
        Mockito.when(applicationUtils.getRequest(Mockito.anyString(), Mockito.anyList()))
                .thenReturn(Mono.just(jsonResponse));

        // When applicationUtils.patchRequest() is called with any String, any List, and any String, return an empty Mono
        Mockito.when(applicationUtils.patchRequest(Mockito.anyString(), Mockito.anyList(), Mockito.anyString()))
                .thenReturn(Mono.empty());

        // Executing the method under test
        StepVerifier.create(orionLDService.saveDid(did,DidMethods.valueOf(didType),userId))
                .expectComplete()
                .verify();

        // Verifying the interactions with the mocks and ensuring that the expected behaviors occurred
        Mockito.verify(applicationUtils).getRequest(Mockito.eq(contextBrokerEntitiesURL + "/api/v1/entities/urn:entities:userId:" + userId), Mockito.anyList());
        Mockito.verify(applicationUtils).patchRequest(Mockito.eq(contextBrokerEntitiesURL + "/api/v1/update"), Mockito.anyList(), Mockito.anyString());
     }
    @Test
    void testGetDids() {
        String userId = "1234";

        // Sample JSON response that the applicationUtils.getRequest() method will be mocked to return
        String jsonResponse = """
                {
                    "id": "urn:entities:userId:1234",
                    "type": "userEntity",
                    "dids": {
                        "type": "Property",
                        "value": [
                            {
                                "type": "key",
                                "value": "did:key:123456"
                            },
                            {
                                "type": "key",
                                "value": "did:key:654321"
                            }
                        ]
                    },
                    "userData": {
                        "type": "Property",
                        "value": {
                            "username": "Manuel",
                            "email": "manuel@gmail.com"
                        }
                    },
                    "vcs": {
                        "type": "Property",
                        "value": []
                    }
                }""";


        // When applicationUtils.getRequest() is called with any String and any List, return a Mono with the jsonResponse
        Mockito.when(applicationUtils.getRequest(Mockito.anyString(), Mockito.anyList()))
                .thenReturn(Mono.just(jsonResponse));

        // List of expected DIDs
        List<String> expectedDids = List.of("did:key:123456", "did:key:654321");

        // Executing the method under test
        StepVerifier.create(orionLDService.getDidsByUserId(userId))
                .assertNext(retrievedDids -> {
                    assertNotNull(retrievedDids, "DIDs list should not be null");
                    assertEquals(expectedDids.size(), retrievedDids.size(), "DIDs list size mismatch");
                    assertTrue(retrievedDids.containsAll(expectedDids), "Mismatch in retrieved DIDs");
                })
                .expectComplete()
                .verify();

        // Verifying the interactions with the mocks and ensuring that the expected behaviors occurred
        Mockito.verify(applicationUtils).getRequest(Mockito.eq(contextBrokerEntitiesURL + "/api/v1/entities/urn:entities:userId:" + userId), Mockito.anyList());
    }


    @Test
    void testDeleteDids() {
        String userId = "1234";
        String did = "did:key:654321";

        // Sample JSON response that the applicationUtils.getRequest() method will be mocked to return
        String jsonResponse = """
                {
                    "id": "urn:entities:userId:1234",
                    "type": "userEntity",
                    "dids": {
                        "type": "Property",
                        "value": [
                            {
                                "type": "key",
                                "value": "did:key:123456"
                            },
                            {
                                "type": "key",
                                "value": "did:key:654321"
                            }
                        ]
                    },
                    "userData": {
                        "type": "Property",
                        "value": {
                            "username": "Manuel",
                            "email": "manuel@gmail.com"
                        }
                    },
                    "vcs": {
                        "type": "Property",
                        "value": []
                    }
                }""";


        // When applicationUtils.getRequest() is called with any String and any List, return a Mono with the jsonResponse
        Mockito.when(applicationUtils.getRequest(Mockito.anyString(), Mockito.anyList()))
                .thenReturn(Mono.just(jsonResponse));

        // When applicationUtils.patchRequest() is called with any String, any List, and any String, return an empty Mono
        Mockito.when(applicationUtils.patchRequest(Mockito.anyString(), Mockito.anyList(), Mockito.anyString()))
                .thenReturn(Mono.empty());

        // Executing the method under test
        StepVerifier.create(orionLDService.deleteSelectedDid(did,userId))
                .expectComplete()
                .verify();

        // Verifying the interactions with the mocks and ensuring that the expected behaviors occurred
        Mockito.verify(applicationUtils).getRequest(Mockito.eq(contextBrokerEntitiesURL + "/api/v1/entities/urn:entities:userId:" + userId), Mockito.anyList());
        Mockito.verify(applicationUtils).patchRequest(Mockito.eq(contextBrokerEntitiesURL + "/api/v1/update"), Mockito.anyList(), Mockito.anyString());
      }
    @Test
    void testGetUserData() {
        String userId = "1234";
        UserAttribute expectedUserData = new UserAttribute("Manuel", "manuel@gmail.com");

        // Sample JSON response that the applicationUtils.getRequest() method will be mocked to return
        String jsonResponse = """
                {
                    "id": "urn:entities:userId:123456",
                    "type": "userEntity",
                    "dids": {
                        "type": "Property",
                        "value": []
                    },
                    "userData": {
                        "type": "Property",
                        "value": {
                            "username": "Manuel",
                            "email": "manuel@gmail.com"
                        }
                    },
                    "vcs": {
                        "type": "Property",
                        "value": []
                    }
                }""";


        // When applicationUtils.getRequest() is called with any String and any List, return a Mono with the jsonResponse
        Mockito.when(applicationUtils.getRequest(Mockito.anyString(), Mockito.anyList()))
                .thenReturn(Mono.just(jsonResponse));

        // Executing the method under test
        StepVerifier.create(orionLDService.getUserDataByUserId(userId))
                .assertNext(retrievedUserData -> {
                    assertNotNull(retrievedUserData, "User data should not be null");
                    assertEquals(expectedUserData.getUsername(), retrievedUserData.getUsername(), "Username mismatch");
                    assertEquals(expectedUserData.getEmail(), retrievedUserData.getEmail(), "Email mismatch");
                })
                .expectComplete()
                .verify();

        // Verifying the interactions with the mocks and ensuring that the expected behaviors occurred
        Mockito.verify(applicationUtils).getRequest(Mockito.eq(contextBrokerEntitiesURL + "/api/v1/entities/urn:entities:userId:" + userId), Mockito.anyList());
    }
    @Test
    void testRegisterUserInContextBroker() {
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setUserId("1234");
        userRequestDTO.setUsername("Manuel");
        userRequestDTO.setEmail("manuel@gmail.com");

        // When applicationUtils.postRequest() is called with expectedUrl, any List, and jsonRequestBody, return a Mono<Void>
        Mockito.when(applicationUtils.postRequest(Mockito.anyString(), Mockito.anyList(), Mockito.anyString()))
                .thenReturn(Mono.empty());

        // Executing the method under test
        StepVerifier.create(orionLDService.registerUserInContextBroker(userRequestDTO))
                .expectComplete()
                .verify();

        // Verifying the interactions with the mocks and ensuring that the expected behaviors occurred
        Mockito.verify(applicationUtils).postRequest(Mockito.eq(contextBrokerEntitiesURL + "/api/v1/publish"), Mockito.anyList(), Mockito.anyString());
    }


}

