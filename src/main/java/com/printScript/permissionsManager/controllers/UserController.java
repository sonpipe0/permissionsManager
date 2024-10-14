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

    @PostMapping("/register")
    public ResponseEntity<Object> createUser(@RequestBody Map<String,Object> body) {
        String email = (String) body.get("email");
        ResponseEntity<Object> response = userService.createUser(email);
        return response;
    }
}
