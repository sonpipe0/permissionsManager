package com.printScript.permissionsManager.controllers;

import com.printScript.permissionsManager.services.SnippetPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller("snippet/permissions")
public class SnippetPermissionController {

    @Autowired
    SnippetPermissionService snippetPermissionService;


    @GetMapping("/hasAccess")
    public boolean hasAccess(String fileName, String user) {
        return snippetPermissionService.hasAccess(fileName, user);
    }
}
