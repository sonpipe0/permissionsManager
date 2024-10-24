package com.printScript.permissionsManager.services;

import com.printScript.permissionsManager.DTO.Error;
import com.printScript.permissionsManager.DTO.Response;
import com.printScript.permissionsManager.entities.GrantType;
import com.printScript.permissionsManager.entities.SnippetPermission;
import com.printScript.permissionsManager.entities.User;
import com.printScript.permissionsManager.entities.UserGrantType;
import com.printScript.permissionsManager.repositories.SnippetPermissionRepository;
import com.printScript.permissionsManager.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SnippetPermissionService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    SnippetPermissionRepository snippetPermissionRepository;

    public Response<Boolean> hasAccess(String snippetId, String userId) {
        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty()) return Response.withError(new Error(404, "User not registered"));
        Optional<SnippetPermission> snippetPermission = snippetPermissionRepository.findById(snippetId);
        if(snippetPermission.isEmpty()) return Response.withError(new Error(404, "Snippet not found"));

        boolean hasAccess = snippetPermission.get().getUserGrantTypes().stream()
                .anyMatch(userGrantType -> userGrantType.getUser().equals(user.get()));
        return Response.withData(hasAccess);
    }

    public Response<String> saveRelation(String snippetId, String userId, GrantType grantType) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) return Response.withError(new Error(404, "User not registered"));

        try {
            // Crear una nueva instancia de SnippetPermission
            SnippetPermission snippetPermission = new SnippetPermission();
            snippetPermission.setSnippetId(snippetId);

            // Crear una nueva instancia de UserGrantType
            UserGrantType userGrantType = new UserGrantType();
            userGrantType.setUser(user.get());
            userGrantType.setSnippetPermission(snippetPermission);
            userGrantType.setGrantType(grantType);

            // Establecer la relación en SnippetPermission
            snippetPermission.setUserGrantTypes(List.of(userGrantType));

            // Guardar la nueva relación
            snippetPermissionRepository.save(snippetPermission);

            return Response.withData("Relationship saved");
        } catch (Exception e) {
            return Response.withError(new Error(500, e.getMessage()));
        }
    }
}
