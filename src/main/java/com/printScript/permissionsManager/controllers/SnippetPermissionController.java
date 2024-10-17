package com.printScript.permissionsManager.controllers;

import com.printScript.permissionsManager.DTO.Response;
import com.printScript.permissionsManager.DTO.SnippetTuple;
import com.printScript.permissionsManager.services.SnippetPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/snippets")
public class SnippetPermissionController {

    @Autowired
    SnippetPermissionService snippetPermissionService;

    @GetMapping("hasAccess")
    public ResponseEntity<Object> hasAccess(@RequestParam String snippetId, @RequestParam String user) {
        boolean access = snippetPermissionService.hasAccess(snippetId, user);
        if (access) {
            return ResponseEntity.ok().build();
        } else {
            return new ResponseEntity<>("Access Denied", HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/save/relationship")
    public ResponseEntity<Object> hasAccessSave(@RequestBody SnippetTuple snippetTuple) {
        Response<Boolean> hasPassed = snippetPermissionService.hasAccessSave(snippetTuple.snippetId(), snippetTuple.userId());
        if (hasPassed.isError()) {
            return new ResponseEntity<>(hasPassed.getError().message(), HttpStatus.valueOf(hasPassed.getError().code()));
        }
        return ResponseEntity.ok().build();
    }
}