package com.printScript.permissionsManager.controllers;

import com.printScript.permissionsManager.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;
    @PostMapping("/create")
    public ResponseEntity<Object> createUser(@RequestBody Map<String,Object> body) {
        String username = (String) body.get("username");
        String password = (String) body.get("password");
        ResponseEntity<Object> response = userService.createUser(username, password);
        return response;
    }
}
