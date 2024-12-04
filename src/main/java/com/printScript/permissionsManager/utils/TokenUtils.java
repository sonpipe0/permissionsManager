package com.printScript.permissionsManager.utils;

import java.io.IOException;
import java.util.Map;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class TokenUtils {
    public static Map<String, String> decodeToken(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        return Map.of("userId", decodedJWT.getSubject(), "username", decodedJWT.getClaim("username").asString());
    }

    public static ResponseBody getUsernames(String token) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url("https://dev-g1sija0qkp7jodd2.auth0.com/api/v2/users").get()
                .addHeader("authorization", "Bearer " + token).addHeader("cache-control", "no-cache").build();

        Response response = client.newCall(request).execute();
        return response.body();
    }

    public static String getUsernameByUserId(String token, String userId) throws IOException {
        ResponseBody responseBody = getUsernames(token);
        if (responseBody == null) {
            throw new IOException("Response body is null");
        }
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode usersNode = objectMapper.readTree(responseBody.string());

        for (JsonNode userNode : usersNode) {
            if (userNode.get("user_id").asText().equals(userId)) {
                return userNode.get("username").asText();
            }
        }

        return null;
    }
}
