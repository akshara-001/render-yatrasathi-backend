package com.yatrasathi.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yatrasathi.dto.response.FlightDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class AmadeusService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${amadeus.client-id}")
    private String clientId;

    @Value("${amadeus.client-secret}")
    private String clientSecret;

    private String accessToken;
    private long tokenExpiry = 0;
    private final Random random = new Random();

    public synchronized String getValidToken() {
        if (accessToken != null && System.currentTimeMillis() < tokenExpiry) {
            return accessToken;
        }
        try {
            String response = webClient.post()
                    .uri("https://test.api.amadeus.com/v1/security/oauth2/token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData("grant_type", "client_credentials")
                            .with("client_id", clientId)
                            .with("client_secret", clientSecret))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(Duration.ofSeconds(10));

            if (response != null) {
                JsonNode root = objectMapper.readTree(response);
                accessToken = root.path("access_token").asText();
                long expiresIn = root.path("expires_in").asLong(1799);
                tokenExpiry = System.currentTimeMillis() + (expiresIn * 1000);
                return accessToken;
            }
        } catch (Exception e) {
            log.error("Failed to acquire Amadeus OAuth token: {}", e.getMessage());
        }
        return null;
    }

    public List<FlightDto> getFlights(String from, String to, String date) {
        String fromUpper = from.toUpperCase();
        String toUpper = to.toUpperCase();

        try {
            String token = getValidToken();
            if (token == null) {
                return getFallbackFlights(fromUpper, toUpper, date);
            }

            String response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("test.api.amadeus.com")
                            .path("/v2/shopping/flight-offers")
                            .queryParam("originLocationCode", fromUpper)
                            .queryParam("destinationLocationCode", toUpper)
                            .queryParam("departureDate", date)
                            .queryParam("adults", 1)
                            .queryParam("currencyCode", "INR")
                            .queryParam("max", 5)
                            .build())
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(Duration.ofSeconds(15));

            if (response == null) {
                return getFallbackFlights(fromUpper, toUpper, date);
            }

            JsonNode root = objectMapper.readTree(response);
            JsonNode dataNode = root.path("data");

            if (!dataNode.isArray() || dataNode.isEmpty()) {
                return getFallbackFlights(fromUpper, toUpper, date);
            }

            List<FlightDto> flights = new ArrayList<>();
            for (JsonNode f : dataNode) {
                String airline = f.path("validatingAirlineCodes").isArray() && !f.path("validatingAirlineCodes").isEmpty()
                        ? f.path("validatingAirlineCodes").get(0).asText()
                        : "Unknown Airline";

                String price = f.path("price").path("total").asText("N/A");

                JsonNode itineraries = f.path("itineraries");
                JsonNode firstItinerary = itineraries.isArray() && !itineraries.isEmpty() ? itineraries.get(0) : null;

                String departure = "Unknown";
                String arrival = "Unknown";
                String duration = "N/A";

                if (firstItinerary != null) {
                    duration = firstItinerary.path("duration").asText("N/A");
                    JsonNode segments = firstItinerary.path("segments");
                    if (segments.isArray() && !segments.isEmpty()) {
                        departure = segments.get(0).path("departure").path("at").asText("Unknown");
                        arrival = segments.get(segments.size() - 1).path("arrival").path("at").asText("Unknown");
                    }
                }

                flights.add(FlightDto.builder()
                        .airline(airline)
                        .price(price)
                        .departure(departure)
                        .arrival(arrival)
                        .duration(duration)
                        .link("https://www.makemytrip.com/flights/")
                        .build());
            }

            return flights;
        } catch (Exception e) {
            log.error("Error fetching Amadeus flights: {}", e.getMessage());
            return getFallbackFlights(fromUpper, toUpper, date);
        }
    }

    public List<FlightDto> getFallbackFlights(String from, String to, String date) {
        List<FlightDto> fallbacks = new ArrayList<>();
        fallbacks.add(FlightDto.builder()
                .airline("IndiGo")
                .price(String.valueOf(4000 + random.nextInt(2000)))
                .departure(date + "T10:00")
                .arrival(date + "T12:30")
                .duration("2h 30m")
                .from(from)
                .to(to)
                .link("https://www.makemytrip.com/flights/")
                .build());

        fallbacks.add(FlightDto.builder()
                .airline("Air India")
                .price(String.valueOf(4500 + random.nextInt(2500)))
                .departure(date + "T14:00")
                .arrival(date + "T16:45")
                .duration("2h 45m")
                .from(from)
                .to(to)
                .link("https://www.makemytrip.com/flights/")
                .build());

        return fallbacks;
    }
}
