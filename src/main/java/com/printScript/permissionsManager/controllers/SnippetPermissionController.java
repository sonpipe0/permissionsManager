package com.printScript.permissionsManager.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.printScript.permissionsManager.DTO.Response;
import com.printScript.permissionsManager.DTO.ShareSnippetDTO;
import com.printScript.permissionsManager.entities.GrantType;
import com.printScript.permissionsManager.services.SnippetPermissionService;
import com.printScript.permissionsManager.services.UserService;
import com.printScript.permissionsManager.utils.TokenUtils;

@RestController
@RequestMapping("/snippets")
public class SnippetPermissionController {

    @Autowired
    SnippetPermissionService snippetPermissionService;

    @Autowired
    UserService userService;

    @GetMapping("has-access")
    public ResponseEntity<Object> hasAccess(@RequestParam String snippetId,
            @RequestHeader Map<String, String> headers) {
        String token = headers.get("authorization").substring(7);
        Map<String, String> userInfo = TokenUtils.decodeToken(token);
        String userId = userInfo.get("userId");
        Response<Boolean> access = snippetPermissionService.hasAccess(snippetId, userId);
        if (!access.getData()) {
            return new ResponseEntity<>("Access Denied", HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("can-edit")
    public ResponseEntity<Object> canEdit(@RequestBody String snippetId, @RequestHeader Map<String, String> headers) {
        String token = headers.get("Authorization").substring(7);
        Map<String, String> userInfo = TokenUtils.decodeToken(token);
        String userId = userInfo.get("userId");
        Response<Boolean> access = snippetPermissionService.canEdit(snippetId, userId);
        if (!access.getData()) {
            return new ResponseEntity<>("Access Denied", HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/save/relationship")
    public ResponseEntity<Object> saveRelation(@RequestBody String snippetId,
            @RequestHeader Map<String, String> headers) {
        String token = headers.get("authorization").substring(7);
        Map<String, String> userInfo = TokenUtils.decodeToken(token);
        String userId = userInfo.get("userId");
        Response<String> hasPassed = snippetPermissionService.saveRelation(snippetId, userId, GrantType.WRITE);
        if (hasPassed.isError()) {
            return new ResponseEntity<>(hasPassed.getError().message(),
                    HttpStatus.valueOf(hasPassed.getError().code()));
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/save/share/relationship")
    public ResponseEntity<Object> saveShareRelation(@RequestBody ShareSnippetDTO shareSnippetDTO,
            @RequestHeader Map<String, String> headers) {
        String token = headers.get("Authorization").substring(7);
        Map<String, String> userInfo = TokenUtils.decodeToken(token);
        String userId = userInfo.get("userId");
        if (!snippetPermissionService.canEdit(shareSnippetDTO.snippetId(), userId).getData()) {
            return new ResponseEntity<>("Access Denied", HttpStatus.FORBIDDEN);
        }
        String shareId = userService.getUserId(shareSnippetDTO.username());
        Response<String> hasPassed = snippetPermissionService.saveRelation(shareSnippetDTO.snippetId(), shareId,
                GrantType.READ);
        if (hasPassed.isError()) {
            return new ResponseEntity<>(hasPassed.getError().message(),
                    HttpStatus.valueOf(hasPassed.getError().code()));
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/get/all/edit")
    public ResponseEntity<Object> getAllSnippetsByUser(@RequestHeader Map<String, String> headers) {
        String token = headers.get("authorization").substring(7);
        Map<String, String> userInfo = TokenUtils.decodeToken(token);
        String userId = userInfo.get("userId");
        Response<List<String>> response = snippetPermissionService.getAllSnippetsByUser(userId);
        if (response.isError()) {
            return new ResponseEntity<>(response.getError().message(), HttpStatus.valueOf(response.getError().code()));
        }
        return ResponseEntity.ok(response.getData());
    }
}
