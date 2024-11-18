package com.printScript.permissionsManager.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.printScript.permissionsManager.entities.GrantType;
import com.printScript.permissionsManager.entities.User;
import com.printScript.permissionsManager.entities.UserGrantType;

public interface UserGrantTypeRepository extends JpaRepository<UserGrantType, Long> {
    List<UserGrantType> findAllByUserAndGrantType(User user, GrantType grantType);

    List<UserGrantType> findAllByUser(User user);
}
