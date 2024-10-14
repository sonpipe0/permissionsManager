package com.printScript.permissionsManager.controllers;

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
    public boolean hasAccess(String snippetId, String user) {
        return snippetPermissionService.hasAccess(snippetId, user);
    }

    @PostMapping("/save/relationship")
    public ResponseEntity<Object> hasAccessSave(@RequestBody SnippetTuple snippetTuple) {
        HashMap<String,Object> response = new HashMap<>();
        response.put("hasPassed",snippetPermissionService.hasAccessSave(snippetTuple.snippetId(), snippetTuple.userId()));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
