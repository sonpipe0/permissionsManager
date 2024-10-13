package com.printScript.permissionsManager.services;


import com.printScript.permissionsManager.entities.SnippetPermission;
import com.printScript.permissionsManager.entities.User;
import com.printScript.permissionsManager.repositories.SnippetPermissionRepository;
import com.printScript.permissionsManager.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
        if (userEntity.isEmpty()) {
            return false;
        }
        Optional<SnippetPermission> snippetPermission = snippetPermissionRepository.findById(snippetId);
        if (snippetPermission.isPresent()) {
            return snippetPermission.get().getUser().equals(userEntity.get());
        } else {
            return false;
        }
    }

    public boolean hasAccessSave(String snippetId, String user) {
        Optional<User> userEntity = userRepository.findById(user);
        if (userEntity.isEmpty()) {
            return false;
        }
        SnippetPermission snippetPermission = new SnippetPermission(snippetId, userEntity.get());
        snippetPermissionRepository.save(snippetPermission);
        return true;
    }

    public ResponseEntity<Object> createUser() {
        User user = new User();
        userRepository.save(user);
        return ResponseEntity.ok(user.getUserId());
    }
}
