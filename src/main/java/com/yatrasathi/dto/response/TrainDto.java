package com.yatrasathi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainDto {
    private String name;
    private String trainNo;
    private Integer price;
    private String from;
    private String to;
    private String date;
    private String link;
}
