package com.printScript.permissionsManager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.printScript.permissionsManager.entities.User;

public interface UserRepository extends JpaRepository<User, String> {
    User findByUsername(String username);
}
