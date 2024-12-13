package com.weather.app.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weather.app.model.WeatherAPIResponse;
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
		boolean isValidPostalCode = isValidPostalCode(postalCode);

		if (!isValidPostalCode) {
			throw new IllegalArgumentException("Invalid postalCode.");
			// prepare invalid input error response
			// Add error logger
		}

		String url = getAPIUrl(postalCode);
		// For this API call to weather bit, we can use circuit breaker 
		String jsonResponse = restTemplate.getForObject(url, String.class);
		WeatherAPIResponse response = prepareWeatherAPIResponse(jsonResponse);

		WeatherResponse weatherResponse = prepareWeatherResponse(postalCode, userName, response);

		persistWeatherHistory(postalCode, userName, weatherResponse);

		return weatherResponse;
	}

	public List<WeatherHistoryResponse> getHistory(String postalCode, String userName) {
		List<WeatherHistory> histories;

		if (postalCode != null) {
			histories = weatherRepository.findByPostalCode(postalCode);
		} else if (userName != null) {
			histories = weatherRepository.findByUserName(userName);
		} else {
			throw new IllegalArgumentException("Either postalCode or user must be provided.");
			// TODO add error logger
		}

		return buildWeatherHistoryResponse(histories);
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
		weatherRepository.save(history);
	}

	private WeatherResponse prepareWeatherResponse(String postalCode, String userName, WeatherAPIResponse response) {
		WeatherResponse weatherResponse = new WeatherResponse();
		weatherResponse.setUserName(userName);
		weatherResponse.setPostalCode(postalCode);

		for (WeatherAPIResponse.Data data : response.getData()) {
			weatherResponse.setHumidity(data.getRh());
			weatherResponse.setTemperature(data.getTemp());
			weatherResponse.setWindDirection(data.getWindDir());
			weatherResponse.setDescription(data.getWeather().getDescription());
		}

		return weatherResponse;
	}

	private WeatherAPIResponse prepareWeatherAPIResponse(String jsonResponse) {
		WeatherAPIResponse response = new WeatherAPIResponse();
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			response = objectMapper.readValue(jsonResponse, WeatherAPIResponse.class);
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
