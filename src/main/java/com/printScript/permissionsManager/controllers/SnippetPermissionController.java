package com.printScript.permissionsManager.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.printScript.permissionsManager.DTO.Response;
import com.printScript.permissionsManager.DTO.SnippetTuple;
import com.printScript.permissionsManager.entities.GrantType;
import com.printScript.permissionsManager.services.SnippetPermissionService;

@RestController
@RequestMapping("/snippets")
public class SnippetPermissionController {

    @Autowired
    SnippetPermissionService snippetPermissionService;

    @GetMapping("hasAccess")
    public ResponseEntity<Object> hasAccess(@RequestParam String snippetId, @RequestParam String userId) {
        Response<Boolean> access = snippetPermissionService.hasAccess(snippetId, userId);
        if (!access.getData()) {
            return new ResponseEntity<>("Access Denied", HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("canEdit")
    public ResponseEntity<Object> canEdit(@RequestParam String snippetId, @RequestParam String userId) {
        Response<Boolean> canEdit = snippetPermissionService.canEdit(snippetId, userId);
        if (!canEdit.getData()) {
            return new ResponseEntity<>("Access Denied", HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/save/relationship")
    public ResponseEntity<Object> saveRelation(@RequestBody SnippetTuple snippetTuple) {
        Response<String> hasPassed = snippetPermissionService.saveRelation(snippetTuple.snippetId(),
                snippetTuple.userId(), GrantType.WRITE);
        if (hasPassed.isError()) {
            return new ResponseEntity<>(hasPassed.getError().message(),
                    HttpStatus.valueOf(hasPassed.getError().code()));
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/save/share/relationship")
    public ResponseEntity<Object> saveShareRelation(@RequestBody SnippetTuple snippetTuple) {
        Response<String> hasPassed = snippetPermissionService.saveRelation(snippetTuple.snippetId(),
                snippetTuple.userId(), GrantType.READ);
        if (hasPassed.isError()) {
            return new ResponseEntity<>(hasPassed.getError().message(),
                    HttpStatus.valueOf(hasPassed.getError().code()));
        }
        return ResponseEntity.ok().build();
    }
}
