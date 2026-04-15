package com.boghdady.springaidemo.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class RestCountriesClient {

    private static final String BASE_URL = "https://restcountries.com/v3.1";
    private final RestTemplate restTemplate = new RestTemplate();

    public String fetchByName(String name) {
        String url = BASE_URL + "/name/" + name
                + "?fields=name,capital,population,currencies,languages,region,flags,area";

        log.debug("Calling RestCountries: {}", url);
        return restTemplate.getForObject(url, String.class);
    }




}
