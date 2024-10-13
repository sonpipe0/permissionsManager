package com.printScript.permissionsManager.controllers;

import com.printScript.permissionsManager.services.SnippetPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    SnippetPermissionService snippetPermissionService;

    @PostMapping("/create")
    public ResponseEntity<Object> createUser() {
        ResponseEntity<Object> response = snippetPermissionService.createUser();
        return response;
    }
}
