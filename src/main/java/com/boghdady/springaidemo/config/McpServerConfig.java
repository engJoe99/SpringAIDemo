package com.boghdady.springaidemo.config;

import com.boghdady.springaidemo.tools.CountryTools;
import com.boghdady.springaidemo.tools.WeatherTools;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpServerConfig {

    @Bean
    public ToolCallbackProvider weatherCountryToolCallbackProvider(WeatherTools weatherTools,
                                                                   CountryTools countryTools) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(weatherTools, countryTools)
                .build();
    }

}
