package com.printScript.permissionsManager.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import com.printScript.permissionsManager.DTO.Response;
import com.printScript.permissionsManager.DTO.ShareSnippetDTO;
import com.printScript.permissionsManager.TestSecurityConfig;
import com.printScript.permissionsManager.entities.GrantType;
import com.printScript.permissionsManager.entities.SnippetPermission;
import com.printScript.permissionsManager.entities.User;
import com.printScript.permissionsManager.entities.UserGrantType;
import com.printScript.permissionsManager.repositories.SnippetPermissionRepository;
import com.printScript.permissionsManager.repositories.UserGrantTypeRepository;
import com.printScript.permissionsManager.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

@ActiveProfiles("test")
@MockitoSettings(strictness = Strictness.LENIENT)
@Import(TestSecurityConfig.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class SnippetPermissionServiceTest {
    @Autowired
    private SnippetPermissionRepository snippetPermissionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserGrantTypeRepository userGrantTypeRepository;

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

        User user = new User();
        user.setUserId("userId");
        user.setUsername("username");
        userRepository.save(user);

        User user1 = new User();
        user1.setUserId("userId1");
        user1.setUsername("username1");
        userRepository.save(user1);

        SnippetPermission snippetPermission = new SnippetPermission();
        snippetPermission.setSnippetId("snippetId");
        snippetPermissionRepository.save(snippetPermission);

        UserGrantType userGrantType = new UserGrantType();
        userGrantType.setGrantType(GrantType.WRITE);
        userGrantType.setUser(user);
        userGrantType.setSnippetPermission(snippetPermission);

        UserGrantType userGrantType1 = new UserGrantType();
        userGrantType1.setGrantType(GrantType.READ);
        userGrantType1.setUser(user1);
        userGrantType1.setSnippetPermission(snippetPermission);

        snippetPermission.setUserGrantTypes(List.of(userGrantType, userGrantType1));
        snippetPermissionRepository.save(snippetPermission);
    }

    @AfterEach
    void tearDown() {
        snippetPermissionRepository.deleteAll();
        userRepository.deleteAll();
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
        assertEquals("snippetId1", snippetPermissionRepository.findById("snippetId1").get().getSnippetId());
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
        Response<String> response = snippetPermissionService.getSnippetAuthor("snippetId");

        assertEquals("username", response.getData());
    }

    @Test
    @Transactional
    void testGetSnippetGrants() {
        SnippetPermissionService.SnippetPermissionGrantResponse snippetPermissionGrantResponse = new SnippetPermissionService.SnippetPermissionGrantResponse("snippetId", "username");

        Response<List<SnippetPermissionService.SnippetPermissionGrantResponse>> response = snippetPermissionService.getSnippetGrants("userId", "ALL");

        assertEquals(1, response.getData().size());
        assertEquals(snippetPermissionGrantResponse, response.getData().getFirst());

        Response<List<SnippetPermissionService.SnippetPermissionGrantResponse>> response1 = snippetPermissionService.getSnippetGrants("userId", "WRITE");

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
        Response<String> response = snippetPermissionService.deleteRelation("snippetId", "userId1");

        assertEquals("Relationship deleted", response.getData());
        assertNull(userGrantTypeRepository.findByUserAndSnippetPermission(userRepository.findById("userId1").get(), snippetPermissionRepository.findById("snippetId").get()));
    }

    @Test
    @Transactional
    void testDeleteAllRelations() {
        Response<String> response = snippetPermissionService.deleteAllRelations("snippetId");

        assertEquals("All relationships deleted", response.getData());
        assertEquals(Optional.empty(), snippetPermissionRepository.findById("snippetId"));
    }

    @Test
    @Transactional
    void testSaveShareRelation() {
        User user = new User();
        user.setUserId("userId2");
        user.setUsername("username2");
        userRepository.save(user);

        ShareSnippetDTO shareSnippetDTO = new ShareSnippetDTO();
        shareSnippetDTO.setSnippetId("snippetId");
        shareSnippetDTO.setUsername("username2");

        Response<String> response = snippetPermissionService.saveShareRelation(shareSnippetDTO, "userId");

        assertEquals("Snippet shared", response.getData());
        assertEquals("username2", userGrantTypeRepository.findByUserAndSnippetPermission(userRepository.findById("userId2").get(), snippetPermissionRepository.findById("snippetId").get()).getUser().getUsername());
        assertEquals(GrantType.READ, userGrantTypeRepository.findByUserAndSnippetPermission(userRepository.findById("userId2").get(), snippetPermissionRepository.findById("snippetId").get()).getGrantType());
    }
}
