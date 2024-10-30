package com.printScript.permissionsManager.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.printScript.permissionsManager.DTO.DependencyDTO;
import com.printScript.permissionsManager.DTO.Error;
import com.printScript.permissionsManager.DTO.Response;
import com.printScript.permissionsManager.entities.Config;
import com.printScript.permissionsManager.entities.User;
import com.printScript.permissionsManager.repositories.ConfigRepository;
import com.printScript.permissionsManager.repositories.UserRepository;

@Service
public class ConfigService {
    @Autowired
    private ConfigRepository configRepository;

    @Autowired
    private UserRepository userRepository;

    public Response<Void> uploadRelationship(DependencyDTO dependencyDTO, String userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty())
            return Response.withError(new Error(404, "User not registered"));
        Config config = new Config();
        config.setUser(user.get());
        config.setConfigType(dependencyDTO.getConfigType());
        configRepository.save(config);

        return Response.withData(null);
    }
}
