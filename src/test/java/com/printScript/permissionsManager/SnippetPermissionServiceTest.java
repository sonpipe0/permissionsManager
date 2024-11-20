package com.printScript.permissionsManager;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.regex.Pattern;

import com.printScript.permissionsManager.DTO.Response;
import com.printScript.permissionsManager.entities.GrantType;
import com.printScript.permissionsManager.entities.SnippetPermission;
import com.printScript.permissionsManager.entities.User;
import com.printScript.permissionsManager.entities.UserGrantType;
import com.printScript.permissionsManager.repositories.SnippetPermissionRepository;
import com.printScript.permissionsManager.repositories.UserGrantTypeRepository;
import com.printScript.permissionsManager.repositories.UserRepository;
import com.printScript.permissionsManager.services.SnippetPermissionService;
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

    private final Pattern uuid = Pattern.compile("([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})");

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
    }

    private String base64Encode(String value) {
        return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(value.getBytes());
    }

    @Test
    void testHasAccess() {
        User user = new User();
        user.setUserId("userId");
        user.setUsername("username");
        userRepository.save(user);

        SnippetPermission snippetPermission = new SnippetPermission();
        snippetPermission.setSnippetId("snippetId");
        snippetPermissionRepository.save(snippetPermission);

        UserGrantType userGrantType = new UserGrantType();
        userGrantType.setGrantType(GrantType.WRITE);
        userGrantType.setUser(user);
        userGrantType.setSnippetPermission(snippetPermission);

        snippetPermission.setUserGrantTypes(List.of(userGrantType));
        snippetPermissionRepository.save(snippetPermission);

        Response<Boolean> response = snippetPermissionService.hasAccess("snippetId", "userId");

        assertTrue(response.getData());
    }
}
