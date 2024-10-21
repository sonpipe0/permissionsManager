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
    public ResponseEntity<Object> hasAccess(@RequestBody SnippetTuple snippetTuple) {
        Response<Boolean> access = snippetPermissionService.hasAccess(snippetTuple.snippetId(), snippetTuple.userId());
        if (!access.getData()) {
            return new ResponseEntity<>("Access Denied", HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/save/relationship")
    public ResponseEntity<Object> saveRelation(@RequestBody SnippetTuple snippetTuple) {
        Response<String> hasPassed = snippetPermissionService.saveRelation(snippetTuple.snippetId(), snippetTuple.userId());
        if (hasPassed.isError()) {
            return new ResponseEntity<>(hasPassed.getError().message(), HttpStatus.valueOf(hasPassed.getError().code()));
        }
        return ResponseEntity.ok().build();
    }
}