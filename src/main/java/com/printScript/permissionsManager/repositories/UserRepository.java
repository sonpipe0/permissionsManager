package com.printScript.permissionsManager.repositories;

import com.printScript.permissionsManager.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, String> {
}