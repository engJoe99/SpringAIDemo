package com.boghdady.springaidemo.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class UserLookupTool {


    // Simulated user database
    private static final Map<String, Map<String, String>> USERS = Map.of(
            "user-101", Map.of("name", "Youssef Alboghdady", "role", "Senior Engineer", "team", "Platform"),
            "user-102", Map.of("name", "Sara Ahmed", "role", "Tech Lead", "team", "AI"),
            "user-103", Map.of("name", "Alex Chen", "role", "DevOps Engineer", "team", "Infrastructure")
    );

    @Tool(description = "Get the profile information of the currently authenticated user")
    public String getMyProfile(ToolContext context) {

        String userId = (String) context.getContext().get("userId");
        log.info("getMyProfile for user ID: {}", userId);

        Map<String, String> profile = USERS.getOrDefault(userId,
                Map.of("name", "Unknown User", "role", "Unknown Role", "team", "Unknown Team"));

        return "{\"userId\":\"" + userId + "\","
                + "\"name\":\"" + profile.get("name") + "\","
                + "\"role\":\"" + profile.get("role") + "\","
                + "\"team\":\"" + profile.get("team") + "\"}";
    }

    @Tool(description = "Lookup a user by name and return their profile information")
    public String lookupUser(@ToolParam(description = "The user name to lookup") String name) {
        log.info("lookupUser: {}", name);

        return USERS.values().stream()
                .filter(profile -> profile.get("name").equalsIgnoreCase(name))
                .findFirst()
                .map(profile -> "{\"name\":\"" + profile.get("name") + "\","
                        + "\"role\":\"" + profile.get("role") + "\","
                        + "\"team\":\"" + profile.get("team") + "\"}")
                .orElse("{\"error\":\"User not found\"}");
    }

}
