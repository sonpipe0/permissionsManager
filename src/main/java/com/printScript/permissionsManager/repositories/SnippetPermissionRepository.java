package com.printScript.permissionsManager.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.printScript.permissionsManager.entities.GrantType;
import com.printScript.permissionsManager.entities.SnippetPermission;

public interface SnippetPermissionRepository extends JpaRepository<SnippetPermission, String> {
    Optional<SnippetPermission> findBySnippetIdAndUserId(String snippetId, String userId);

    List<SnippetPermission> findAllByUserId(String userId);

    List<SnippetPermission> findAllByUserIdAndGrantType(String userId, GrantType grantType);

    List<SnippetPermission> findAllBySnippetId(String snippetId);

    Optional<SnippetPermission> findBySnippetIdAndGrantType(String snippetId, GrantType grantType);
}
