package com.printScript.permissionsManager.services;

import static com.printScript.permissionsManager.utils.TokenUtils.getUsernameByUserId;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;

import com.printScript.permissionsManager.DTO.Response;
import com.printScript.permissionsManager.DTO.ShareSnippetDTO;
import com.printScript.permissionsManager.DTO.SnippetPermissionGrantResponse;
import com.printScript.permissionsManager.TestSecurityConfig;
import com.printScript.permissionsManager.entities.GrantType;
import com.printScript.permissionsManager.entities.SnippetPermission;
import com.printScript.permissionsManager.repositories.SnippetPermissionRepository;
import com.printScript.permissionsManager.utils.TokenUtils;

import jakarta.transaction.Transactional;

@ActiveProfiles("test")
@MockitoSettings(strictness = Strictness.LENIENT)
@Import(TestSecurityConfig.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class SnippetPermissionServiceTest {
    @Autowired
    private SnippetPermissionRepository snippetPermissionRepository;

    @Autowired
    private SnippetPermissionService snippetPermissionService;

    private String mockToken;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        Jwt jwt = mock(Jwt.class);

        String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String payload = "{\"sub\":\"mockUserId\",\"username\":\"mockUsername\",\"role\":\"user\",\"iat\":1609459200}";
        String signature = "mockSignature";

        mockToken = base64Encode(header) + "." + base64Encode(payload) + "." + signature;
        mockToken = "Bearer " + mockToken;

        when(jwt.getTokenValue()).thenReturn(mockToken);
        when(jwt.getClaim("sub")).thenReturn("mockUserId");
        when(jwt.getClaim("username")).thenReturn("mockUsername");
        when(jwt.getClaim("role")).thenReturn("user");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(jwt);

        SecurityContextHolder.setContext(securityContext);

        SnippetPermission snippetPermission = new SnippetPermission();
        snippetPermission.setSnippetId("snippetId");
        snippetPermission.setUserId("userId");
        snippetPermission.setGrantType(GrantType.WRITE);
        snippetPermissionRepository.save(snippetPermission);

        SnippetPermission snippetPermission2 = new SnippetPermission();
        snippetPermission2.setSnippetId("snippetId2");
        snippetPermission2.setUserId("userId2");
        snippetPermission2.setGrantType(GrantType.READ);
        snippetPermissionRepository.save(snippetPermission2);
    }

    @AfterEach
    void tearDown() {
        snippetPermissionRepository.deleteAll();
    }

    private String base64Encode(String value) {
        return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(value.getBytes());
    }

    @Test
    @Transactional
    void testHasAccess() {
        Response<Boolean> response = snippetPermissionService.hasAccess("snippetId", "userId");

        assertTrue(response.getData());
    }

    @Test
    void saveRelation() {
        Response<String> response = snippetPermissionService.saveRelation("snippetId1", "userId", GrantType.WRITE);

        assertEquals("Relationship saved", response.getData());
        assertEquals("snippetId1",
                snippetPermissionRepository.findBySnippetIdAndUserId("snippetId1", "userId").get().getSnippetId());

        Response<String> response2 = snippetPermissionService.saveRelation("snippetId1", "userId", GrantType.WRITE);

        assertEquals(409, response2.getError().code());
    }

    @Test
    @Transactional
    void testCanEdit() {
        Response<Boolean> response = snippetPermissionService.canEdit("snippetId", "userId");

        assertTrue(response.getData());
    }

    @Test
    @Transactional
    void testGetSnippetAuthor() {
        try (MockedStatic<TokenUtils> mockedTokenUtils = mockStatic(TokenUtils.class)) {
            mockedTokenUtils.when(() -> getUsernameByUserId(anyString(), anyString())).thenReturn("username");

            Response<String> response = snippetPermissionService.getSnippetAuthor("snippetId", mockToken);

            assertEquals("username", response.getData());
        }
    }

    @Test
    @Transactional
    void testGetSnippetGrants() {
        SnippetPermissionGrantResponse snippetPermissionGrantResponse = new SnippetPermissionGrantResponse("snippetId",
                "userId");

        Response<List<SnippetPermissionGrantResponse>> response = snippetPermissionService.getSnippetGrants("userId",
                "ALL");

        assertEquals(1, response.getData().size());
        assertEquals(snippetPermissionGrantResponse, response.getData().getFirst());

        Response<List<SnippetPermissionGrantResponse>> response1 = snippetPermissionService.getSnippetGrants("userId",
                "WRITE");

        assertEquals(1, response1.getData().size());
        assertEquals(snippetPermissionGrantResponse, response1.getData().getFirst());
    }

    @Test
    void testGetAllSnippetsByUser() {
        Response<List<String>> response = snippetPermissionService.getAllSnippetsByUser("userId");

        assertEquals(1, response.getData().size());
        assertEquals("snippetId", response.getData().getFirst());
    }

    @Test
    @Transactional
    void testDeleteRelation() {
        Response<String> response = snippetPermissionService.deleteRelation("snippetId", "userId");

        assertEquals("Relationship deleted", response.getData());
        assertEquals(Optional.empty(), snippetPermissionRepository.findBySnippetIdAndUserId("snippetId", "userId"));
    }

    @Test
    @Transactional
    void testDeleteAllRelations() {
        Response<String> response = snippetPermissionService.deleteAllRelations("snippetId");

        assertEquals("All relationships deleted", response.getData());
        assertEquals(Optional.empty(), snippetPermissionRepository.findBySnippetIdAndUserId("snippetId", "userId"));
    }

    @Test
    @Transactional
    void testSaveShareRelation() {
        ShareSnippetDTO shareSnippetDTO = new ShareSnippetDTO();
        shareSnippetDTO.setSnippetId("snippetId");
        shareSnippetDTO.setUserId("userId2");

        Response<String> response = snippetPermissionService.saveShareRelation(shareSnippetDTO, "userId");

        assertEquals("Snippet shared", response.getData());
        assertNotNull(snippetPermissionRepository.findBySnippetIdAndUserId("snippetId", "userId2"));
    }

    @Test
    void testSaveRelation_AlreadyExists() {
        snippetPermissionService.saveRelation("snippetId", "userId", GrantType.WRITE);
        Response<String> response = snippetPermissionService.saveRelation("snippetId", "userId", GrantType.WRITE);
        assertEquals(409, response.getError().code());
    }

    @Test
    void testGetSnippetAuthor_NotFound() {
        Response<String> response = snippetPermissionService.getSnippetAuthor("nonExistentSnippetId", mockToken);
        assertEquals(404, response.getError().code());
    }

    @Test
    void testDeleteRelation_NotFound() {
        Response<String> response = snippetPermissionService.deleteRelation("nonExistentSnippetId", "userId");
        assertEquals(404, response.getError().code());
    }

    @Test
    void testDeleteAllRelations_NotFound() {
        Response<String> response = snippetPermissionService.deleteAllRelations("nonExistentSnippetId");
        assertEquals(404, response.getError().code());
    }

    @Test
    void testSaveShareRelation_NoEditPermission() {
        ShareSnippetDTO shareSnippetDTO = new ShareSnippetDTO();
        shareSnippetDTO.setSnippetId("snippetId2");
        shareSnippetDTO.setUserId("userId2");

        Response<String> response = snippetPermissionService.saveShareRelation(shareSnippetDTO, "userId");
        Response<String> response2 = snippetPermissionService.saveShareRelation(shareSnippetDTO, "userId");

        assertEquals(404, response2.getError().code());
    }

    @Test
    void testHasNoAccess() {
        Response<Boolean> response = snippetPermissionService.hasAccess("nonExistentSnippetId", "userId");
        assertEquals(404, response.getError().code());
    }
}
