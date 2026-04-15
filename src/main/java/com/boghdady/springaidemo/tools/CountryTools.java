package com.boghdady.springaidemo.tools;

import com.boghdady.springaidemo.client.RestCountriesClient;
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
public class CountryTools {

    private final RestCountriesClient restCountriesClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Tool(
            name = "get_country_info",
            description = "Gets detailed information about a country including: " +
                    "capital city, population, region, official languages, currency, and area. " +
                    "Use when the user asks about a country — its capital, currency, " +
                    "population size, official language, geographic region, or flag."
    )
    public String getCountryInfo(
            @ToolParam(description = "Country name or partial name, e.g. 'Egypt', 'Germany', 'United States'") String countryName) {

        log.info("get_country_info called for: {}", countryName);

        try {
            String rawJson = restCountriesClient.fetchByName(countryName);
            JsonNode countries = objectMapper.readTree(rawJson);

            if (!countries.isArray() || countries.isEmpty()) {
                return ToolResult.error("No country found matching: " + countryName).toString();
            }

            // Take the first (best) match
            JsonNode country = countries.get(0);

            String officialName = country.path("name").path("official").asText("N/A");
            String commonName = country.path("name").path("common").asText(countryName);
            String region = country.path("region").asText("N/A");

            // Capital (array)
            String capital = "N/A";
            if (country.path("capital").isArray() && !country.path("capital").isEmpty()) {
                capital = country.path("capital").get(0).asText();
            }

            // Population
            long population = country.path("population").asLong(0);

            // Area
            double area = country.path("area").asDouble(0);

            // Currencies (map)
            StringBuilder currencies = new StringBuilder();
            country.path("currencies").fields().forEachRemaining(entry -> {
                String symbol = entry.getValue().path("symbol").asText("");
                String name = entry.getValue().path("name").asText(entry.getKey());
                currencies.append(name).append(" (").append(symbol).append(") ");
            });

            // Languages (map)
            StringBuilder languages = new StringBuilder();
            country.path("languages").fields().forEachRemaining(entry ->
                    languages.append(entry.getValue().asText()).append(", "));
            String langStr = languages.toString().replaceAll(", $", "");

            // Flag emoji
            String flagEmoji = country.path("flags").path("alt").asText("");
            String flagUrl   = country.path("flags").path("png").asText("");

            String result = String.format("""
                    🌍 Country: %s (%s)
                    ──────────────────────────────────────
                    🏛  Official Name : %s
                    📍  Capital       : %s
                    🌐  Region        : %s
                    👥  Population    : %,d
                    📏  Area          : %,.0f km²
                    💰  Currency      : %s
                    🗣  Languages     : %s
                    🏳  Flag          : %s

                    Source: RestCountries (restcountries.com) — free & open
                    """,
                    commonName, region, officialName, capital,
                    region, population, area,
                    currencies.toString().trim(),
                    langStr, flagUrl);

            return ToolResult.ok(result).toString();

        } catch (Exception e) {
            log.error("get_country_info failed for {}: {}", countryName, e.getMessage(), e);
            return ToolResult.error("Failed to fetch info for " + countryName + ": " + e.getMessage()).toString();
        }
    }

}
