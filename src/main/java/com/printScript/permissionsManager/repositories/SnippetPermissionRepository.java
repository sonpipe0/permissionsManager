package com.printScript.permissionsManager.repositories;

import com.printScript.permissionsManager.entities.SnippetPermission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SnippetPermissionRepository extends JpaRepository<SnippetPermission, String> {

}
