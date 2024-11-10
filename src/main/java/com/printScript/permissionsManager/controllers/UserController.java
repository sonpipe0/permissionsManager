package com.printScript.permissionsManager.controllers;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.printScript.permissionsManager.DTO.Response;
import com.printScript.permissionsManager.DTO.UserDTO;
import com.printScript.permissionsManager.services.UserService;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/paginated")
    public ResponseEntity<Object> getUsersPaginated(@RequestParam String page, @RequestParam String pageSize,
            @RequestParam String prefix) {
        Integer size = Integer.parseInt(pageSize);
        Integer index = Integer.parseInt(page);
        Response<List<UserDTO>> response = userService.getUsersPaginated(size, index, prefix);
        if (response.getError() != null) {
            return ResponseEntity.status(500).body(response.getError());
        }
        HashMap<String, Object> data = new HashMap<>();
        data.put("page", page);
        data.put("page_size", pageSize);
        data.put("count", response.getData().size());
        data.put("users", response.getData());
        return ResponseEntity.ok(data);
    }
}
