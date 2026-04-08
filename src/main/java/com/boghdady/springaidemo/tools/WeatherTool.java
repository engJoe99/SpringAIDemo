package com.boghdady.springaidemo.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WeatherTool {

    @Tool(description = "Get the weather for a city, e.g. Cairo, Retrieve The Temperature In Celsius, Condition, Humidity")
    public String getWeather(@ToolParam(description = "The city name, e.g. cairo, london") String city) {

        log.info("getWeather: {}", city);


        // ------- Simulate a weather API call -------
        return switch (city.toLowerCase()) {
            case "cairo" -> "25°C, Clear, 80% Humidity";
            case "london" -> "20°C, Rain, 60% Humidity";
            default -> "Unknown city";
        };
    }
}
