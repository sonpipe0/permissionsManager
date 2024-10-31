package com.printScript.permissionsManager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.printScript.permissionsManager.entities.Config;

public interface ConfigRepository extends JpaRepository<Config, String> {
}
