package com.rahul.journal_app.service;

import com.rahul.journal_app.model.response.WeatherResponse;
import com.rahul.journal_app.cache.AppCache;
import com.rahul.journal_app.constants.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
@Slf4j
public class WeatherService {

    @Value("${weather.api.key}")
    private String apiKey;
    @Value("${weather.api.url}")
    private String baseUrl;

    @Autowired
    private AppCache appCache;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RedisService redisService;




    public WeatherResponse getWeather(String city) {
        city = city.toLowerCase();
        String weatherOfCity="weather_of_"+city;
        WeatherResponse weatherResponse=redisService.get(weatherOfCity, WeatherResponse.class);
        if(weatherResponse != null){
            return weatherResponse;
        }
        log.info("Fetching weather data for city: {}", city);
        String finalUrl = baseUrl.replace(Constants.CITY, city).replace(Constants.WEATHER_API_KEY, apiKey);
        log.debug("Constructed URL: {}", finalUrl);

        /*String url=appCache.propertiesMap.get(Constants.WEATHER_API_URL);
        String apiKey=appCache.propertiesMap.get(Constants.WEATHER_API_KEY);
        String finalUrl = url.replace(Constants.CITY, city).replace(Constants.WEATHER_API_KEY, apiKey);*/

        try {
            ResponseEntity<WeatherResponse> response = restTemplate.exchange(finalUrl, HttpMethod.GET, null, WeatherResponse.class);
            log.info("Successfully fetched weather data for city: {}", city);
            WeatherResponse body = response.getBody();
            if(body !=null){
                redisService.set(weatherOfCity, body, 300l);
            }
            return body;
        } catch (Exception e) {
            log.error("Error occurred while fetching weather data for city: {}. Error: {}", city, e.getMessage(), e);
        }

        log.warn("Returning null for city: {} as no weather data could be fetched.", city);
        return null;
    }
}
