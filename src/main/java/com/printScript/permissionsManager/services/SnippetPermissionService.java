package com.printScript.permissionsManager.services;

import com.printScript.permissionsManager.DTO.Error;
import com.printScript.permissionsManager.DTO.Response;
import com.printScript.permissionsManager.entities.SnippetPermission;
import com.printScript.permissionsManager.entities.User;
import com.printScript.permissionsManager.repositories.SnippetPermissionRepository;
import com.printScript.permissionsManager.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

        boolean hasAccess = snippetPermission.get().getUser().equals(user.get());
        return Response.withData(hasAccess);
    }

    public Response<String> saveRelation(String snippetId, String userId) {
        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty()) return Response.withError(new Error(404, "User not registered"));
        try {
            SnippetPermission snippetPermission = new SnippetPermission(snippetId, user.get());
            snippetPermissionRepository.save(snippetPermission);
            return Response.withData("Relationship saved");
        } catch (Exception e) {
            return Response.withError(new Error(500, e.getMessage()));
        }
    }
}
