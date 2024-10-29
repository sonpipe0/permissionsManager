package com.printScript.permissionsManager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.printScript.permissionsManager.entities.SnippetPermission;

public interface SnippetPermissionRepository extends JpaRepository<SnippetPermission, String> {
}
