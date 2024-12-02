package com.printScript.permissionsManager.repositories;

import com.printScript.permissionsManager.entities.GrantType;
import org.springframework.data.jpa.repository.JpaRepository;

import com.printScript.permissionsManager.entities.SnippetPermission;

import java.util.List;
import java.util.Optional;

public interface SnippetPermissionRepository extends JpaRepository<SnippetPermission, String> {
    Optional<SnippetPermission> findBySnippetIdAndUserId(String snippetId, String userId);

    List<SnippetPermission> findAllByUserId(String userId);

    List<SnippetPermission> findAllByUserIdAndGrantType(String userId, GrantType grantType);

    List<SnippetPermission> findAllBySnippetId(String snippetId);
}
