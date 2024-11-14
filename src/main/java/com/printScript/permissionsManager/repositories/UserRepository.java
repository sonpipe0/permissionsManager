package com.printScript.permissionsManager.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.printScript.permissionsManager.entities.User;

public interface UserRepository extends JpaRepository<User, String> {
    User findByUsername(String username);

    List<User> getUsersByUsernameStartingWith(String prefix, Pageable pageable);
}
