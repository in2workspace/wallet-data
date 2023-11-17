package es.in2.wallet.data.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.wallet.data.api.model.*;
import es.in2.wallet.data.api.service.impl.UserDataServiceImpl;
import es.in2.wallet.data.api.utils.DidMethods;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@WebFluxTest(UserDataServiceImpl.class)
class UserDataServiceImplTest {

    private UserDataServiceImpl userDataServiceImpl;
    private ObjectMapper objectMapper;


    @BeforeEach
    void setUp() {
        userDataServiceImpl = new UserDataServiceImpl();
        objectMapper = new ObjectMapper();
    }
    

    @Test
    void testSaveVC() throws JsonProcessingException {
        // Sample JWT token for a verifiable credential
        String vcJwt = "eyJraWQiOiJkaWQ6a2V5OnpRM3NodGNFUVAzeXV4YmtaMVNqTjUxVDhmUW1SeVhuanJYbThFODRXTFhLRFFiUm4jelEzc2h0Y0VRUDN5dXhia1oxU2pONTFUOGZRbVJ5WG5qclhtOEU4NFdMWEtEUWJSbiIsInR5cCI6IkpXVCIsImFsZyI6IkVTMjU2SyJ9.eyJzdWIiOiJkaWQ6a2V5OnpEbmFlZnk3amhwY0ZCanp0TXJFSktFVHdFU0NoUXd4cEpuVUpLb3ZzWUQ1ZkpabXAiLCJuYmYiOjE2OTgxMzQ4NTUsImlzcyI6ImRpZDprZXk6elEzc2h0Y0VRUDN5dXhia1oxU2pONTFUOGZRbVJ5WG5qclhtOEU4NFdMWEtEUWJSbiIsImV4cCI6MTcwMDcyNjg1NSwiaWF0IjoxNjk4MTM0ODU1LCJ2YyI6eyJ0eXBlIjpbIlZlcmlmaWFibGVDcmVkZW50aWFsIiwiTEVBUkNyZWRlbnRpYWwiXSwiQGNvbnRleHQiOlsiaHR0cHM6Ly93d3cudzMub3JnLzIwMTgvY3JlZGVudGlhbHMvdjEiLCJodHRwczovL2RvbWUtbWFya2V0cGxhY2UuZXUvLzIwMjIvY3JlZGVudGlhbHMvbGVhcmNyZWRlbnRpYWwvdjEiXSwiaWQiOiJ1cm46dXVpZDo4NzAwYmVlNS00NjIxLTQ3MjAtOTRkZS1lODY2ZmI3MTk3ZTkiLCJpc3N1ZXIiOnsiaWQiOiJkaWQ6a2V5OnpRM3NodGNFUVAzeXV4YmtaMVNqTjUxVDhmUW1SeVhuanJYbThFODRXTFhLRFFiUm4ifSwiaXNzdWFuY2VEYXRlIjoiMjAyMy0xMC0yNFQwODowNzozNVoiLCJpc3N1ZWQiOiIyMDIzLTEwLTI0VDA4OjA3OjM1WiIsInZhbGlkRnJvbSI6IjIwMjMtMTAtMjRUMDg6MDc6MzVaIiwiZXhwaXJhdGlvbkRhdGUiOiIyMDIzLTExLTIzVDA4OjA3OjM1WiIsImNyZWRlbnRpYWxTdWJqZWN0Ijp7ImlkIjoiZGlkOmtleTp6RG5hZWZ5N2pocGNGQmp6dE1yRUpLRVR3RVNDaFF3eHBKblVKS292c1lENWZKWm1wIiwidGl0bGUiOiJNci4iLCJmaXJzdF9uYW1lIjoiSm9obiIsImxhc3RfbmFtZSI6IkRvZSIsImdlbmRlciI6Ik0iLCJwb3N0YWxfYWRkcmVzcyI6IiIsImVtYWlsIjoiam9obmRvZUBnb29kYWlyLmNvbSIsInRlbGVwaG9uZSI6IiIsImZheCI6IiIsIm1vYmlsZV9waG9uZSI6IiszNDc4NzQyNjYyMyIsImxlZ2FsUmVwcmVzZW50YXRpdmUiOnsiY24iOiI1NjU2NTY1NlYgSmVzdXMgUnVpeiIsInNlcmlhbE51bWJlciI6IjU2NTY1NjU2ViIsIm9yZ2FuaXphdGlvbklkZW50aWZpZXIiOiJWQVRFUy0xMjM0NTY3OCIsIm8iOiJHb29kQWlyIiwiYyI6IkVTIn0sInJvbGVzQW5kRHV0aWVzIjpbeyJ0eXBlIjoiTEVBUkNyZWRlbnRpYWwiLCJpZCI6Imh0dHBzOi8vZG9tZS1tYXJrZXRwbGFjZS5ldS8vbGVhci92MS82NDg0OTk0bjRyOWU5OTA0OTQifV0sImtleSI6InZhbHVlIn19LCJqdGkiOiJ1cm46dXVpZDo4NzAwYmVlNS00NjIxLTQ3MjAtOTRkZS1lODY2ZmI3MTk3ZTkifQ.2_YNY515CaohirD4AHDBMvzDagEn-p8uAsaiMT0H4ltK2uVfG8IWWqV_OOR6lFlXMzUhJd7nKsaWkhnAQY8kyA";
        // Sample JSON response entity
        String userEntityJson = """
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
        UserEntity userEntity = objectMapper.readValue(userEntityJson, UserEntity.class);
        // Executing the method under test
        StepVerifier.create(userDataServiceImpl.saveVC(userEntity, vcJwt))
                .expectNextMatches(userEntityResult -> userEntityResult.getVcs() != null && userEntityResult.getVcs().getValue().size() == 2)
                .expectComplete()
                .verify();
    }
    @Test
    void testGetUserVCsInJson() throws JsonProcessingException {
        String userEntityJson = """
            {
                "id": "urn:entities:userId:1234",
                "type": "userEntity",
                "userData": {
                    "type": "Property",
                    "value": {
                        "username": "John Doe",
                        "email": "john@gmail.com"
                    }
                },
                "dids": {
                    "type": "Property",
                    "value": []
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

        UserEntity userEntity = objectMapper.readValue(userEntityJson, UserEntity.class);
        // Executing the method under test
        StepVerifier.create(userDataServiceImpl.getUserVCsInJson(userEntity))
                .assertNext(vcBasicDataDTOList -> {
                    assertFalse(vcBasicDataDTOList.isEmpty(), "The VC list should not be empty");

                    assertEquals("vc1", vcBasicDataDTOList.get(0).getId());
                    assertTrue(vcBasicDataDTOList.get(0).getVcType().contains("SpecificCredentialType"));

                    assertEquals("vc2", vcBasicDataDTOList.get(1).getId());
                    assertTrue(vcBasicDataDTOList.get(1).getVcType().contains("LEARCredential"));
                })
                .expectComplete()
                .verify();

        }
    @Test
    void testGetSelectableVCsByVcTypeList() throws JsonProcessingException {
        // Arrange
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
        UserEntity userEntity = objectMapper.readValue(userEntityJson, UserEntity.class);
        // Act and Assert
        StepVerifier.create(userDataServiceImpl.getSelectableVCsByVcTypeList(vcTypeList, userEntity))
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

        }
    @Test
    void tesDeleteVC() throws JsonProcessingException {
        // Sample JWT token for a verifiable credential
        String vcID = "vc2";
        String expectedDid = "did:key123";

        // Sample JSON response that the applicationUtils.getRequest() method will be mocked to return
        String userEntityJson = """
            {
                "id": "urn:entities:userId:1234",
                "type": "userEntity",
                "dids": {
                    "type": "Property",
                    "value": [
                            {
                                "type": "key",
                                "value": "did:key:123"
                            },
                            {
                                "type": "key",
                                "value": "did:key:654"
                            }
                        ]
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
                                    "id": "did:key:123",
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
                                    "id": "did:key:654",
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
        UserEntity userEntity = objectMapper.readValue(userEntityJson, UserEntity.class);

        // Executing the method under test
        StepVerifier.create(userDataServiceImpl.deleteVerifiableCredential(userEntity,vcID,expectedDid))
                .assertNext(userEntity1 -> {
                    assertTrue(userEntity1.getDids().getValue().stream().noneMatch(didAttr -> didAttr.getValue().equals(expectedDid)),
                            "The DID associated with VC id " + vcID + " should be deleted");
                    assertFalse(userEntity1.getVcs().getValue().stream().anyMatch(vcAttribute -> vcAttribute.getId().equals(vcID)),
                            "The VC with id " + vcID + " should be deleted");
                })
                .expectComplete()
                .verify();
    }
    @Test
    void testSaveDid() throws JsonProcessingException {
        // Sample JWT token for a verifiable credential
        String did = "did:key:1234";
        String didType = "KEY";

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
                            "username": "Manuel",
                            "email": "manuel@gmail.com"
                        }
                    },
                    "vcs": {
                        "type": "Property",
                        "value": []
                    }
                }""";
        UserEntity userEntity = objectMapper.readValue(userEntityJson, UserEntity.class);
        // Executing the method under test
        StepVerifier.create(userDataServiceImpl.saveDid(userEntity,did, DidMethods.valueOf(didType)))
                .expectNextMatches(userEntityResult -> userEntityResult.getDids() != null && userEntityResult.getDids().getValue().size() == 1)
                .expectComplete()
                .verify();
     }
    @Test
    void testGetDids() throws JsonProcessingException {
        // Sample JSON response that the applicationUtils.getRequest() method will be mocked to return
        String userEntityJson = """
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

        UserEntity userEntity = objectMapper.readValue(userEntityJson, UserEntity.class);
        // List of expected DIDs
        List<String> expectedDids = List.of("did:key:123456", "did:key:654321");

        // Executing the method under test
        StepVerifier.create(userDataServiceImpl.getDidsByUserEntity(userEntity))
                .assertNext(retrievedDids -> {
                    assertNotNull(retrievedDids, "DIDs list should not be null");
                    assertEquals(expectedDids.size(), retrievedDids.size(), "DIDs list size mismatch");
                    assertTrue(retrievedDids.containsAll(expectedDids), "Mismatch in retrieved DIDs");
                })
                .expectComplete()
                .verify();
    }


    @Test
    void testDeleteDids() throws JsonProcessingException {
        String did = "did:key:654321";

        // Sample JSON response that the applicationUtils.getRequest() method will be mocked to return
        String userEntityJson = """
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

        UserEntity userEntity = objectMapper.readValue(userEntityJson, UserEntity.class);

        // Executing the method under test
        StepVerifier.create(userDataServiceImpl.deleteSelectedDidFromUserEntity(did,userEntity))
                .assertNext(userEntity1 -> assertFalse(
                        userEntity1.getDids().getValue().stream().anyMatch(didAttribute -> didAttribute.getValue().equals(did)),
                        "The did with value " + did + " should be deleted"
                ))
                .expectComplete()
                .verify();

      }
    @Test
    void testGetUserData() throws JsonProcessingException {
        UserAttribute expectedUserData = new UserAttribute("Manuel", "manuel@gmail.com");

        // Sample JSON response that the applicationUtils.getRequest() method will be mocked to return
        String userEntityJson = """
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

        UserEntity userEntity = objectMapper.readValue(userEntityJson, UserEntity.class);
        // Executing the method under test
        StepVerifier.create(userDataServiceImpl.getUserDataFromUserEntity(userEntity))
                .assertNext(retrievedUserData -> {
                    assertNotNull(retrievedUserData, "User data should not be null");
                    assertEquals(expectedUserData.getUsername(), retrievedUserData.getUsername(), "Username mismatch");
                    assertEquals(expectedUserData.getEmail(), retrievedUserData.getEmail(), "Email mismatch");
                })
                .expectComplete()
                .verify();
    }
    @Test
    void testRegisterUserInContextBroker() {
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setUserId("1234");
        userRequestDTO.setUsername("Manuel");
        userRequestDTO.setEmail("manuel@gmail.com");

        // Executing the method under test
        StepVerifier.create(userDataServiceImpl.createUserEntity(userRequestDTO))
                .assertNext(userEntity -> {
                    assertNotNull(userEntity, "User Entity should not be null");
                    assertEquals(userEntity.getUserData().getValue().getUsername(), userRequestDTO.getUsername(), "User name mismatch");
                })
                .expectComplete()
                .verify();
    }


}

