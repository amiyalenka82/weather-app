package com.weather.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.weather.app.model.ErrorResponse;
import com.weather.app.model.WeatherHistoryResponse;
import com.weather.app.model.WeatherResponse;
import com.weather.app.service.WeatherService;
import com.weather.app.util.ErrorConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for handling weather-related endpoints.
 */
@RestController
@RequestMapping("/api/v1/weather")
public class WeatherController {

    private static final Logger logger = LoggerFactory.getLogger(WeatherController.class);

    private final WeatherService weatherService;

    @Autowired
    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    /**
     * Retrieves the current weather for a given postal code or user.
     * 
     * @param postalCode the postal code to get the weather for
     * @param userName   the user's name to associate with the request
     * @return ResponseEntity containing weather data or an error response
     */
    @GetMapping("/current")
    public ResponseEntity<?> getWeather(
            @RequestParam(required = false) String postalCode,
            @RequestParam(required = false) String userName) {
        try {
            logger.info("Received request to fetch weather. PostalCode: {}, UserName: {}", postalCode, userName);

            WeatherResponse weatherResponse = weatherService.getWeather(postalCode, userName);

            if (weatherResponse.getErrorResponse() != null) {
                logger.warn("Error occurred while fetching weather: {}", weatherResponse.getErrorResponse().getErrorMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(weatherResponse.getErrorResponse());
            }

            return ResponseEntity.ok(weatherResponse);

        } catch (Exception e) {
            logger.error("Unexpected error while fetching weather for postalCode: {}, userName: {}", postalCode, userName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse(ErrorConstants.INTERNAL_SERVER_ERROR_CODE, ErrorConstants.INTERNAL_SERVER_ERROR_MESSAGE));
        }
    }

    /**
     * Retrieves weather history for a given postal code or user.
     * 
     * @param postalCode the postal code to get the history for
     * @param userName   the user's name to get the history for
     * @return ResponseEntity containing the history data or an error response
     */
    @GetMapping("/history")
    public ResponseEntity<?> getHistory(
            @RequestParam(required = false) String postalCode,
            @RequestParam(required = false) String userName) {
        try {
            logger.info("Received request to fetch weather history. PostalCode: {}, UserName: {}", postalCode, userName);

            List<WeatherHistoryResponse> historyResponses = weatherService.getHistory(postalCode, userName);

            if (historyResponses.isEmpty() || historyResponses.get(0).getErrorResponse() != null) {
                logger.warn("No valid history found. Returning error response.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse(ErrorConstants.BAD_REQUEST_ERROR_CODE, ErrorConstants.BAD_REQUEST_ERROR_MESSAGE));
            }

            return ResponseEntity.ok(historyResponses);

        } catch (Exception e) {
            logger.error("Unexpected error while fetching history for postalCode: {}, userName: {}", postalCode, userName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse(ErrorConstants.INTERNAL_SERVER_ERROR_CODE, ErrorConstants.INTERNAL_SERVER_ERROR_MESSAGE));
        }
    }

    /**
     * Utility method to create an error response.
     * 
     * @param errorCode    the error code
     * @param errorMessage the error message
     * @return ErrorResponse object
     */
    private ErrorResponse createErrorResponse(String errorCode, String errorMessage) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(errorCode);
        errorResponse.setErrorMessage(errorMessage);
        return errorResponse;
    }
}
