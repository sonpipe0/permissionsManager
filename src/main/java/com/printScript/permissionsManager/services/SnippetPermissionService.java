package com.printScript.permissionsManager.services;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import com.printScript.permissionsManager.DTO.ShareSnippetDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.printScript.permissionsManager.DTO.Error;
import com.printScript.permissionsManager.DTO.Response;
import com.printScript.permissionsManager.entities.GrantType;
import com.printScript.permissionsManager.entities.SnippetPermission;
import com.printScript.permissionsManager.entities.User;
import com.printScript.permissionsManager.entities.UserGrantType;
import com.printScript.permissionsManager.repositories.SnippetPermissionRepository;
import com.printScript.permissionsManager.repositories.UserGrantTypeRepository;
import com.printScript.permissionsManager.repositories.UserRepository;

@Service
public class SnippetPermissionService {

    private static final Logger logger = Logger.getLogger(SnippetPermissionService.class.getName());

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    SnippetPermissionRepository snippetPermissionRepository;

    @Autowired
    UserGrantTypeRepository userGrantTypeRepository;

    public Response<Boolean> hasAccess(String snippetId, String userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty())
            return Response.withError(new Error(404, "User not found"));
        Optional<SnippetPermission> snippetPermission = snippetPermissionRepository.findById(snippetId);
        if (snippetPermission.isEmpty())
            return Response.withError(new Error(404, "Snippet not found"));
        boolean hasAccess = snippetPermission.get().getUserGrantTypes().stream()
                .anyMatch(userGrantType -> userGrantType.getUser().equals(user.get()));
        return Response.withData(hasAccess);
    }

    public Response<String> saveRelation(String snippetId, String userId, GrantType grantType) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty())
            return Response.withError(new Error(404, "User not found"));
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

    public Response<Boolean> canEdit(String snippetId, String userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty())
            return Response.withError(new Error(404, "User not found"));
        Optional<SnippetPermission> snippetPermission = snippetPermissionRepository.findById(snippetId);
        if (snippetPermission.isEmpty())
            return Response.withError(new Error(404, "Snippet not found"));
        boolean canEdit = snippetPermission.get().getUserGrantTypes().stream()
                .anyMatch(userGrantType -> userGrantType.getUser().equals(user.get())
                        && userGrantType.getGrantType().equals(GrantType.WRITE));
        return Response.withData(canEdit);
    }

    public Response<List<String>> getAllSnippetsByUser(String userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return Response.withError(new Error(404, "User not found"));
        }
        List<UserGrantType> snippetIds = userGrantTypeRepository.findAllByUserAndGrantType(user.get(), GrantType.WRITE);
        return Response.withData(snippetIds.stream().map(UserGrantType::getSnippetPermission)
                .map(SnippetPermission::getSnippetId).toList());
}

    public Response<String> saveShareRelation(ShareSnippetDTO shareSnippetDTO, String userId) {
        Response<Boolean> canEdit = canEdit(shareSnippetDTO.getSnippetId(), userId);
        if (canEdit.isError())
            return Response.withError(canEdit.getError());
        if (!canEdit.getData()) {
            return Response.withError(new Error(403, "Share Access Denied"));
        }
        try {
            Response<String> userResponse = userService.getUserId(shareSnippetDTO.getUsername());
            if (userResponse.isError())
                return userResponse;
            String shareId = userResponse.getData();
            Response<String> response = saveRelation(shareSnippetDTO.getSnippetId(), shareId, GrantType.READ);
            if (response.isError())
                return response;
        } catch (Exception e) {
            return Response.withError(new Error(500, e.getMessage()));
        }
        return Response.withData("Snippet shared");
    }
}
