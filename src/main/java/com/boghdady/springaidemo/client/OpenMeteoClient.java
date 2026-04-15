package com.boghdady.springaidemo.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
public class OpenMeteoClient {

    public static final String BASE_URL = "https://api.open-meteo.com/v1/forecast";
    private final RestTemplate restTemplate = new RestTemplate();

    /*
    * Fetches current weather + 3-day forecast for given coordinates
    */
    public String fetchWeather(double latitude, double longitude) {
        String url = UriComponentsBuilder.fromUriString(BASE_URL)
                .queryParam("latitude", latitude)
                .queryParam("longitude", longitude)
                .queryParam("current_weather", true)
                .queryParam("daily", "temperature_2m_max,temperature_2m_min,precipitation_sum,windspeed_10m_max")
                .queryParam("timezone", "auto")
                .queryParam("forecast_days", 3)
                .toUriString();

        log.debug("Calling Open-Meteo: {}", url);
        return restTemplate.getForObject(url, String.class);
    }


}
