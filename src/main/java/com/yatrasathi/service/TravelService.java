package com.yatrasathi.service;

import com.yatrasathi.dto.response.BusDto;
import com.yatrasathi.dto.response.FlightDto;
import com.yatrasathi.dto.response.TrainDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TravelService {

    private final AmadeusService amadeusService;

    private static final Map<String, String> DHAM_AIRPORT_MAP = Map.of(
            "Badrinath", "DED",
            "Kedarnath", "DED",
            "Gangotri", "DED",
            "Yamunotri", "DED",
            "Dwarka", "JGA",
            "Puri", "BBI",
            "Rameswaram", "TRZ"
    );

    private static final Map<String, List<TrainTemplate>> DHAM_TRAINS = Map.of(
            "Badrinath", List.of(
                    new TrainTemplate("Dehradun Express", "12001", 650),
                    new TrainTemplate("Uttarakhand Sampark Kranti", "15035", 780)
            ),
            "Kedarnath", List.of(
                    new TrainTemplate("Kedarnath Special", "15045", 720),
                    new TrainTemplate("Haridwar Express", "14042", 600)
            ),
            "Gangotri", List.of(
                    new TrainTemplate("Yamuna Express", "14510", 640),
                    new TrainTemplate("Himalaya Mail", "14232", 700)
            ),
            "Yamunotri", List.of(
                    new TrainTemplate("Shatabdi to Dehradun", "12017", 800),
                    new TrainTemplate("Mussoorie Express", "14041", 580)
            ),
            "Dwarka", List.of(
                    new TrainTemplate("Dwarka Express", "15636", 900),
                    new TrainTemplate("Jamnagar Superfast", "22906", 1050)
            ),
            "Puri", List.of(
                    new TrainTemplate("Puri Express", "18410", 980),
                    new TrainTemplate("Bhubaneswar SF", "22820", 890)
            ),
            "Rameswaram", List.of(
                    new TrainTemplate("Rameswaram Express", "16779", 1150),
                    new TrainTemplate("Madurai Passenger", "56725", 760)
            )
    );

    private static final Map<String, List<BusTemplate>> DHAM_BUSES = Map.of(
            "Badrinath", List.of(
                    new BusTemplate("Uttarakhand Travels", "UK01-EXP", 550),
                    new BusTemplate("Himalaya Deluxe", "UK02-AC", 750)
            ),
            "Dwarka", List.of(
                    new BusTemplate("Saurashtra Express", "GJ01-DWK", 980),
                    new BusTemplate("Jamnagar Luxury", "GJ03-JAM", 1200)
            ),
            "Puri", List.of(
                    new BusTemplate("Odisha Travels", "OD01-PUR", 700),
                    new BusTemplate("SeaCoast Express", "OD02-LUX", 950)
            ),
            "Rameswaram", List.of(
                    new BusTemplate("TNSTC", "TN63-RMM", 850),
                    new BusTemplate("SouthLine Travels", "TN60-DELUXE", 1100)
            )
    );

    private record TrainTemplate(String name, String trainNo, int price) {}
    private record BusTemplate(String operator, String busNo, int price) {}

    public List<FlightDto> getFlights(String from, String to, String date) {
        String destination = DHAM_AIRPORT_MAP.getOrDefault(to, to);
        return amadeusService.getFlights(from, destination, date);
    }

    public List<TrainDto> getTrains(String from, String to, String date) {
        List<TrainTemplate> templates = DHAM_TRAINS.getOrDefault(to, List.of(
                new TrainTemplate("Bharat Express", "10001", 700)
        ));

        List<TrainDto> result = new ArrayList<>();
        for (TrainTemplate t : templates) {
            result.add(TrainDto.builder()
                    .name(t.name())
                    .trainNo(t.trainNo())
                    .price(t.price())
                    .from(from)
                    .to(to)
                    .date(date)
                    .link("https://www.irctc.co.in/")
                    .build());
        }
        return result;
    }

    public List<BusDto> getBuses(String from, String to, String date) {
        List<BusTemplate> templates = DHAM_BUSES.getOrDefault(to, List.of(
                new BusTemplate("Bharat Express", "DLX001", 700)
        ));

        List<BusDto> result = new ArrayList<>();
        for (BusTemplate b : templates) {
            result.add(BusDto.builder()
                    .operator(b.operator())
                    .busNo(b.busNo())
                    .price(b.price())
                    .from(from)
                    .to(to)
                    .date(date)
                    .link("https://www.redbus.in/")
                    .build());
        }
        return result;
    }
}
