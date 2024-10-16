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

    public boolean hasAccess(String snippetId, String user) {
        Optional<User> userEntity = userRepository.findById(user);
        if(userEntity.isEmpty()) return false;
        Optional<SnippetPermission> snippetPermission = snippetPermissionRepository.findById(snippetId);
        if(snippetPermission.isEmpty()) return false;
        return snippetPermission.get().getUser().equals(userEntity.get());
    }

    public Response<Boolean> hasAccessSave(String snippetId, String user) {
        if(!isRegistered(user)) return Response.withError(new Error(404, "User not registered"));
        try {
            User userEntity = userRepository.findById(user).get();
            SnippetPermission snippetPermission = new SnippetPermission(snippetId, userEntity);
            snippetPermissionRepository.save(snippetPermission);
            return Response.withData(true);
        } catch (Exception e) {
            return Response.withError(new Error(500, e.getMessage()));
        }
    }

    private boolean isRegistered(String user) {
        Optional<User> userEntity = userRepository.findById(user);
        return userEntity.isPresent();
    }
}
