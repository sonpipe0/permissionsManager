package com.printScript.permissionsManager.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.printScript.permissionsManager.DTO.DependencyDTO;
import com.printScript.permissionsManager.DTO.Response;
import com.printScript.permissionsManager.services.ConfigService;
import com.printScript.permissionsManager.utils.TokenUtils;

@RestController
@RequestMapping("/config")
public class ConfigController {

    @Autowired
    private ConfigService formatPermissionService;

    @PostMapping("/upload")
    public ResponseEntity<Object> uploadConfig(DependencyDTO dependencyDTO,
            @RequestHeader Map<String, String> headers) {
        String token = headers.get("Authorization").substring(7);
        Map<String, String> userInfo = TokenUtils.decodeToken(token);
        String userId = userInfo.get("userId");
        Response<Void> response = formatPermissionService.uploadRelationship(dependencyDTO, userId);
        if (response.isError())
            return new ResponseEntity<>(response.getError().message(),
                    HttpStatusCode.valueOf(response.getError().code()));
        return ResponseEntity.ok().build();
    }
}
