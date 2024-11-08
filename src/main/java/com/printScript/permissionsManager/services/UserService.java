package com.printScript.permissionsManager.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.printScript.permissionsManager.DTO.Error;
import com.printScript.permissionsManager.DTO.Response;
import com.printScript.permissionsManager.repositories.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public Response<String> getUserId(String username) {
        try {
            return Response.withData(userRepository.findByUsername(username).getUserId());
        } catch (Exception e) {
            return Response.withError(new Error(404, "User not found"));
        }
    }
}
