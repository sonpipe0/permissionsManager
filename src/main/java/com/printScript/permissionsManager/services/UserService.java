package com.printScript.permissionsManager.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.printScript.permissionsManager.DTO.Error;
import com.printScript.permissionsManager.DTO.Response;
import com.printScript.permissionsManager.DTO.UserDTO;
import com.printScript.permissionsManager.entities.User;
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

    public Response<List<UserDTO>> getUsersPaginated(Integer size, Integer index, String prefix) {
        List<User> users = userRepository.getUsersByUsernameStartingWith(prefix, PageRequest.of(index, size));
        List<UserDTO> userDTOS = users.stream().map(user -> {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getUserId());
            userDTO.setName(user.getUsername());
            return userDTO;
        }).toList();
        return Response.withData(userDTOS);
    }
}
