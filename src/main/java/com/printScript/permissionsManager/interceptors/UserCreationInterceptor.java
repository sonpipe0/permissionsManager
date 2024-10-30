package com.printScript.permissionsManager.interceptors;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.printScript.permissionsManager.entities.User;
import com.printScript.permissionsManager.repositories.UserRepository;
import com.printScript.permissionsManager.utils.TokenUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class UserCreationInterceptor implements HandlerInterceptor {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String token = request.getHeader("Authorization").substring(7);
        Map<String, String> userInfo = TokenUtils.decodeToken(token);
        String userId = userInfo.get("userId");
        String username = userInfo.get("username");

        if (!userRepository.existsById(userId)) {
            User user = new User();
            user.setUserId(userId);
            user.setUsername(username);
            userRepository.save(user);
        }

        return true;
    }
}
