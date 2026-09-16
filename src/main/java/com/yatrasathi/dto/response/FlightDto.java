package com.yatrasathi.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FlightDto {
    private String airline;
    private String price;
    private String departure;
    private String arrival;
    private String duration;
    private String link;
    private String from;
    private String to;
}
