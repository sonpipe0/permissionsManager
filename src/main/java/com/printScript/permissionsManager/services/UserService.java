package com.printScript.permissionsManager.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.printScript.permissionsManager.repositories.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public String getUserId(String username) {
        return userRepository.findByUsername(username).getUserId();
    }
}
