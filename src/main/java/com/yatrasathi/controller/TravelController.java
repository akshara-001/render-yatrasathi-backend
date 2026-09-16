package com.yatrasathi.controller;

import com.yatrasathi.dto.response.ApiResponse;
import com.yatrasathi.dto.response.BusDto;
import com.yatrasathi.dto.response.FlightDto;
import com.yatrasathi.dto.response.TrainDto;
import com.yatrasathi.service.TravelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/travel")
@RequiredArgsConstructor
public class TravelController {

    private final TravelService travelService;

    @GetMapping("/flights")
    public ResponseEntity<?> getFlights(
            @RequestParam(value = "from", required = false) String fromParam,
            @RequestParam(value = "origin", required = false) String originParam,
            @RequestParam(value = "to", required = false) String toParam,
            @RequestParam(value = "destination", required = false) String destinationParam,
            @RequestParam(value = "date", required = false) String date) {

        String from = fromParam != null ? fromParam : originParam;
        String to = toParam != null ? toParam : destinationParam;

        if (from == null || from.isBlank() || to == null || to.isBlank() || date == null || date.isBlank()) {
            log.info("Missing required fields: from={}, to={}, date={}", from, to, date);
            return ResponseEntity.badRequest().body(ApiResponse.error("Missing required fields"));
        }

        try {
            List<FlightDto> flights = travelService.getFlights(from, to, date);
            return ResponseEntity.ok(flights);
        } catch (Exception e) {
            log.error("Flights route error: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Error fetching flights", e.getMessage()));
        }
    }

    @GetMapping("/trains")
    public ResponseEntity<?> getTrains(
            @RequestParam(value = "from", required = false) String from,
            @RequestParam(value = "to", required = false) String to,
            @RequestParam(value = "date", required = false) String date) {

        if (from == null || from.isBlank() || to == null || to.isBlank() || date == null || date.isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Missing required fields"));
        }

        try {
            List<TrainDto> trains = travelService.getTrains(from, to, date);
            return ResponseEntity.ok(trains);
        } catch (Exception e) {
            log.error("Trains route error: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Error fetching trains", e.getMessage()));
        }
    }

    @GetMapping("/buses")
    public ResponseEntity<?> getBuses(
            @RequestParam(value = "from", required = false) String from,
            @RequestParam(value = "to", required = false) String to,
            @RequestParam(value = "date", required = false) String date) {

        if (from == null || from.isBlank() || to == null || to.isBlank() || date == null || date.isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Missing required fields"));
        }

        try {
            List<BusDto> buses = travelService.getBuses(from, to, date);
            return ResponseEntity.ok(buses);
        } catch (Exception e) {
            log.error("Buses route error: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Error fetching buses", e.getMessage()));
        }
    }
}
