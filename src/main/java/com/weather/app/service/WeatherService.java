package com.weather.app.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weather.app.model.WeatherbitAPIResponse;
import com.weather.app.model.ErrorResponse;
import com.weather.app.model.WeatherHistory;
import com.weather.app.model.WeatherHistoryResponse;
import com.weather.app.model.WeatherResponse;
import com.weather.app.repository.WeatherRepository;

@Service
public class WeatherService {

    @Autowired
    private WeatherRepository weatherRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${weatherbit.api.key}")
    private String apiKey;

    @Value("${weatherbit.api.url}")
    private String apiUrl;

    @Value("${weatherbit.country}")
    private String country;

    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);

    private static final String ERROR_INVALID_POSTAL_CODE = "Invalid postal code";
    private static final String ERROR_MISSING_INPUT = "Either postalCode or userName must be provided.";
    private static final String ERROR_API_CALL = "Failed to fetch weather data from Weatherbit API.";
    private static final String ERROR_HISTORY_PERSIST = "Failed to persist weather history.";

    private static final String ERROR_CODE_BAD_REQUEST = "400";

    /**
     * Fetches the current weather data based on the postal code.
     *
     * @param postalCode the postal code to fetch weather for
     * @param userName   the user requesting the weather information
     * @return WeatherResponse object with weather data or error information
     */
    public WeatherResponse getWeather(String postalCode, String userName) {
        WeatherResponse weatherResponse = new WeatherResponse();

        if (!isValidPostalCode(postalCode)) {
            logger.error(ERROR_INVALID_POSTAL_CODE);
            weatherResponse.setErrorResponse(getErrorResponse(ERROR_CODE_BAD_REQUEST, ERROR_INVALID_POSTAL_CODE));
            return weatherResponse;
        }

        String url = getAPIUrl(postalCode);
        WeatherbitAPIResponse weatherbitAPIResponse;

        try {
            String jsonResponse = restTemplate.getForObject(url, String.class);
            weatherbitAPIResponse = prepareWeatherBitAPIResponse(jsonResponse);
        } catch (RestClientException e) {
            logger.error(ERROR_API_CALL, e);
            weatherResponse.setErrorResponse(getErrorResponse(ERROR_CODE_BAD_REQUEST, ERROR_API_CALL));
            return weatherResponse;
        }

        prepareWeatherResponse(postalCode, userName, weatherbitAPIResponse, weatherResponse);
        persistWeatherHistory(postalCode, userName, weatherResponse);

        return weatherResponse;
    }

    /**
     * Fetches the weather history based on postal code or user name.
     *
     * @param postalCode the postal code to fetch history for
     * @param userName   the user whose history is requested
     * @return a list of WeatherHistoryResponse objects
     */
    public List<WeatherHistoryResponse> getHistory(String postalCode, String userName) {
        List<WeatherHistory> histories;

        if (postalCode != null) {
            histories = weatherRepository.findByPostalCode(postalCode);
        } else if (userName != null) {
            histories = weatherRepository.findByUserName(userName);
        } else {
            logger.error(ERROR_MISSING_INPUT);
            List<WeatherHistoryResponse> errorResponse = new ArrayList<>();
            WeatherHistoryResponse weatherHistoryResponse = new WeatherHistoryResponse();
            weatherHistoryResponse.setErrorResponse(getErrorResponse(ERROR_CODE_BAD_REQUEST, ERROR_MISSING_INPUT));
            errorResponse.add(weatherHistoryResponse);
            return errorResponse;
        }

        return buildWeatherHistoryResponse(histories);
    }

    /**
     * Persists weather history in the database.
     *
     * @param postalCode      the postal code associated with the weather data
     * @param userName        the user who requested the data
     * @param weatherResponse the weather response to persist
     */
    void persistWeatherHistory(String postalCode, String userName, WeatherResponse weatherResponse) {
        WeatherHistory history = new WeatherHistory();
        history.setTimestamp(LocalDateTime.now());
        history.setPostalCode(postalCode);
        history.setUserName(userName);
        history.setTemperature(weatherResponse.getTemperature());
        history.setHumidity(weatherResponse.getHumidity());
        history.setWeatherCondition(weatherResponse.getDescription());

        try {
            weatherRepository.save(history);
            logger.info("Weather history persisted successfully for postalCode: {}, userName: {}", postalCode, userName);
        } catch (RuntimeException e) {
            logger.error(ERROR_HISTORY_PERSIST, e);
        }
    }

    /**
     * Prepares the weather response based on API data.
     *
     * @param postalCode      the postal code
     * @param userName        the user name
     * @param apiResponse     the response from Weatherbit API
     * @param weatherResponse the weather response object to populate
     */
    void prepareWeatherResponse(String postalCode, String userName, WeatherbitAPIResponse apiResponse, WeatherResponse weatherResponse) {
        weatherResponse.setPostalCode(postalCode);
        weatherResponse.setUserName(userName);

        if (apiResponse.getData() != null && !apiResponse.getData().isEmpty()) {
            WeatherbitAPIResponse.Data data = apiResponse.getData().get(0);
            weatherResponse.setTemperature(data.getTemp());
            weatherResponse.setHumidity(data.getRh());
            weatherResponse.setWindDirection(data.getWindDir());
            weatherResponse.setDescription(data.getWeather().getDescription());
        }
    }

    WeatherbitAPIResponse prepareWeatherBitAPIResponse(String jsonResponse) {
        try {
            return new ObjectMapper().readValue(jsonResponse, WeatherbitAPIResponse.class);
        } catch (Exception e) {
            logger.error("Failed to parse Weatherbit API response.", e);
            throw new RuntimeException("Invalid API response format.");
        }
    }

    private String getAPIUrl(String postalCode) {
        return String.format("%s?postal_code=%s&country=%s&key=%s", apiUrl, postalCode, country, apiKey);
    }

    private boolean isValidPostalCode(String postalCode) {
        String postalCodePattern = "^[0-9]{5}(?:-[0-9]{4})?$";
        return postalCode != null && Pattern.matches(postalCodePattern, postalCode.trim());
    }

    private ErrorResponse getErrorResponse(String errorCode, String errorMessage) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(errorCode);
        errorResponse.setErrorMessage(errorMessage);
        return errorResponse;
    }

    private List<WeatherHistoryResponse> buildWeatherHistoryResponse(List<WeatherHistory> histories) {
        return histories.stream().map(history -> {
            WeatherHistoryResponse response = new WeatherHistoryResponse();
            response.setPostalCode(history.getPostalCode());
            response.setUserName(history.getUserName());
            response.setTemperature(history.getTemperature());
            response.setHumidity(history.getHumidity());
            response.setWeatherCondition(history.getWeatherCondition());
            return response;
        }).collect(Collectors.toList());
    }
}
