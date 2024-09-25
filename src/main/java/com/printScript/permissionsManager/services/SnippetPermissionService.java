package com.printScript.permissionsManager.services;


import com.printScript.permissionsManager.entities.User;
import com.printScript.permissionsManager.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SnippetPermissionService {

    @Autowired
    WebClientService webClientService;

    @Autowired
    UserRepository userRepository;

    public boolean hasAccess(String fileName, String user) {

        User userEntity = userRepository.findByEmail(user);
        String url = "http://localhost:8080/permissions?fileName=" + fileName + "&user=" + user;
        String response = webClientService.get(url).block();
        switch (response) {
            case "true":
                return true;
            case "false":
                return false;
            default:
                throw new RuntimeException("Unexpected response from permissions service: " + response);
        }
    }
}
