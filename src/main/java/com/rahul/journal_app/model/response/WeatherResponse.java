package com.rahul.journal_app.model.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Data
@NoArgsConstructor
public class WeatherResponse{

    private Location location;
    private Current current;


    @Getter
    @Setter
    public class Current{

        private int temperature;
        @JsonProperty("weather_descriptions")
        private List<String> weatherDescriptions;
        private int feelslike;

    }

    @Getter
    @Setter
    public class Location{
        private String name;
        private String country;
        private String region;
        private String timezone_id;
        private String localtime;
    }


//    location": {
//            "name": "Bhopal",
//            "country": "India",
//            "region": "Madhya Pradesh",
//            "lat": "23.267",
//            "lon": "77.400",
//            "timezone_id": "Asia/Kolkata",
//            "localtime": "2025-04-16 10:05",
//            "localtime_epoch": 1744797900,
//            "utc_offset": "5.50"



}





