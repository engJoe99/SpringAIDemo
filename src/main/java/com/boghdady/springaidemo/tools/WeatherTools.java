package com.boghdady.springaidemo.tools;

import com.boghdady.springaidemo.client.OpenMeteoClient;
import com.boghdady.springaidemo.model.ToolResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherTools {

    private final OpenMeteoClient openMeteoClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Tool(
            name = "get_current_weather",
            description = "Gets the current weather and a 3-day forecast for a given location. " +
                    "Returns temperature, wind speed, precipitation, and weather condition. " +
                    "Use when the user asks: What's the weather like? Is it raining? " +
                    "How hot/cold is it in [city]? What should I wear today?"
    )
    public String getCurrentWeather(
            @ToolParam(description = "Latitude of the location, e.g. 30.0444 for Cairo") double latitude,
            @ToolParam(description = "Longitude of the location, e.g. 31.2357 for Cairo") double longitude,
            @ToolParam(description = "Human-readable location name for display, e.g. Cairo, Egypt") String locationName) {

        log.info("get_current_weather called for {} ({}, {})", locationName, latitude, longitude);

        try {
            String rawJson = openMeteoClient.fetchWeather(latitude, longitude);
            JsonNode root = objectMapper.readTree(rawJson);

            // Extract current weather
            JsonNode current = root.path("current_weather");
            double tempC = current.path("temperature").asDouble();
            double windKph = current.path("windspeed").asDouble();
            int weatherCode = current.path("weathercode").asInt();
            String condition = decodeWeatherCode(weatherCode);

            // Extract daily forecast
            JsonNode daily = root.path("daily");
            JsonNode times = daily.path("time");
            JsonNode maxTemps = daily.path("temperature_2m_max");
            JsonNode minTemps = daily.path("temperature_2m_min");
            JsonNode precipitation = daily.path("precipitation_sum");

            StringBuilder forecast = new StringBuilder();
            for (int i = 0; i < times.size(); i++) {
                forecast.append(String.format("%s → High:%.1f°C | Low:%.1f°C | Rain:%.1fmm\n",
                        times.get(i).asText(),
                        maxTemps.get(i).asDouble(),
                        minTemps.get(i).asDouble(),
                        precipitation.get(i).asDouble()));
            }

            String result = String.format("""
                    📍 Weather for %s
                    ──────────────────────────────
                    🌡  Current Temperature : %.1f°C
                    💨  Wind Speed          : %.1f km/h
                    🌤  Condition           : %s

                    📅 3-Day Forecast:
                    %s
                    Source: Open-Meteo (open-meteo.com) — free & open
                    """,
                    locationName, tempC, windKph, condition, forecast);

            return ToolResult.ok(result).toString();

        } catch (Exception e) {
            log.error("get_current_weather failed for {}: {}", locationName, e.getMessage(), e);
            return ToolResult.error("Failed to fetch weather for " + locationName + ": " + e.getMessage()).toString();
        }
    }

    /**
     * Decodes WMO Weather Interpretation Codes (WW) used by Open-Meteo.
     * Full list: <a href="https://open-meteo.com/en/docs#weathervariables">...</a>
     */
    private String decodeWeatherCode(int code) {
        return switch (code) {
            case 0             -> "Clear sky";
            case 1, 2, 3       -> "Partly cloudy";
            case 45, 48        -> "Foggy";
            case 51, 53, 55    -> "Drizzle";
            case 61, 63, 65    -> "Rain";
            case 71, 73, 75    -> "Snow";
            case 80, 81, 82    -> "Rain showers";
            case 95            -> "Thunderstorm";
            case 96, 99        -> "Thunderstorm with hail";
            default            -> "Unknown (code: " + code + ")";
        };
    }


}
