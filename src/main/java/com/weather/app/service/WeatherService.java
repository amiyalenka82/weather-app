package com.weather.app.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
	String country;

	public WeatherResponse getWeather(String postalCode, String userName) {
		WeatherResponse weatherResponse = new WeatherResponse();
		boolean isValidPostalCode = isValidPostalCode(postalCode);
		if (!isValidPostalCode) {
			ErrorResponse errorResponse = getErrorResponse("400", "Invalid postal code");
			// TODO move the constants to a constant or config file
			// TODO add error logger
			weatherResponse.setErrorResponse(errorResponse);
			return weatherResponse;
		}

		String url = getAPIUrl(postalCode);
		
		// TODO For this API call to weather bit, use circuit breaker for resiliency
		String jsonResponse = restTemplate.getForObject(url, String.class);
		
		WeatherbitAPIResponse weatherbitAPIResponse = prepareWeatherBitAPIResponse(jsonResponse);

		prepareWeatherResponse(postalCode, userName, weatherbitAPIResponse, weatherResponse);

		persistWeatherHistory(postalCode, userName, weatherResponse);

		return weatherResponse;
	}

	public List<WeatherHistoryResponse> getHistory(String postalCode, String userName) {
		List<WeatherHistory> histories = null;

		if (postalCode != null) {
			histories = weatherRepository.findByPostalCode(postalCode);
		} else if (userName != null) {
			histories = weatherRepository.findByUserName(userName);
		} else {
			List<WeatherHistoryResponse> historyResponses = new ArrayList<WeatherHistoryResponse>();
			ErrorResponse errorResponse = getErrorResponse("400", "Either postalCode or user must be provided.");
			// TODO move the constants to a constant or config file
			
			WeatherHistoryResponse weatherHistoryResponse = new WeatherHistoryResponse();
			weatherHistoryResponse.setErrorResponse(errorResponse);
			historyResponses.add(weatherHistoryResponse);
			
			return historyResponses;
			
			// TODO add error logger
		}
		return  buildWeatherHistoryResponse(histories);
	}

	private ErrorResponse getErrorResponse(String errorCode, String errorMessage) {
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setErrorCode(errorCode);
		errorResponse.setErrorMessage(errorMessage);
		return errorResponse;
	}

	private List<WeatherHistoryResponse> buildWeatherHistoryResponse(List<WeatherHistory> histories) {
		return histories.stream().map(history -> {
			WeatherHistoryResponse WeatherHistoryResponse = new WeatherHistoryResponse();
			WeatherHistoryResponse.setPostalCode(history.getPostalCode());
			WeatherHistoryResponse.setUser(history.getUserName());
			WeatherHistoryResponse.setTemperature(history.getTemperature());
			WeatherHistoryResponse.setHumidity(history.getHumidity());
			WeatherHistoryResponse.setWeatherCondition(history.getWeatherCondition());
			return WeatherHistoryResponse;
		}).collect(Collectors.toList());
	}
	
	private void persistWeatherHistory(String postalCode, String userName, WeatherResponse weatherResponse) {
		WeatherHistory history = new WeatherHistory();
		history.setTimestamp(LocalDateTime.now());
		history.setPostalCode(postalCode);
		history.setUserName(userName);
		history.setTemperature(weatherResponse.getTemperature());
		history.setHumidity(weatherResponse.getHumidity());
		history.setWeatherCondition(weatherResponse.getDescription());
		try {
			weatherRepository.save(history);
		} catch (RuntimeException exception) {
			// TODO add logger error for the failed ones
			// Prepare ErrorResponse with appropriate error code and error message
		}
		
		// TODO add logger info for persisting into DB
	}

	private void prepareWeatherResponse(String postalCode, String userName, WeatherbitAPIResponse weatherbitAPIResponse, WeatherResponse weatherResponse) {
		weatherResponse.setUserName(userName);
		weatherResponse.setPostalCode(postalCode);

		for (WeatherbitAPIResponse.Data data : weatherbitAPIResponse.getData()) {
			weatherResponse.setHumidity(data.getRh());
			weatherResponse.setTemperature(data.getTemp());
			weatherResponse.setWindDirection(data.getWindDir());
			weatherResponse.setDescription(data.getWeather().getDescription());
		}
	}

	private WeatherbitAPIResponse prepareWeatherBitAPIResponse(String jsonResponse) {
		WeatherbitAPIResponse response = new WeatherbitAPIResponse();
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			response = objectMapper.readValue(jsonResponse, WeatherbitAPIResponse.class);
		} catch (Exception e) {
			e.printStackTrace();
			// TODO add exception logger
		}
		return response;
	}

	private String getAPIUrl(String postalCode) {
		String url = apiUrl + "?postal_code=" + postalCode + "&country=" + country + "&key=" + apiKey;
		return url;
	}

	// Method to validate the postal code
	private boolean isValidPostalCode(String postalCode) {
		// Define the regex pattern for postal code
		String postalCodePattern = "^[0-9]{5}(?:-[0-9]{4})?$"; // US ZIP Code format: 12345 or 12345-6789

		// Check if postal code is null or empty
		if (postalCode == null || postalCode.trim().isEmpty()) {
			return false;
		}

		// Use regex to validate the postal code
		return Pattern.matches(postalCodePattern, postalCode);
	}
}
