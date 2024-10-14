package com.printScript.permissionsManager.services;

import com.printScript.permissionsManager.entities.User;
import com.printScript.permissionsManager.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public ResponseEntity<Object> createUser(String email) {
        User user = new User();
        user.setEmail(email);
        userRepository.save(user);
        return ResponseEntity.ok(user.getUserId());
    }
}
