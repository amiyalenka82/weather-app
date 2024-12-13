package com.weather.app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import com.weather.app.model.WeatherHistory;
import com.weather.app.model.WeatherHistoryResponse;
import com.weather.app.model.WeatherResponse;
import com.weather.app.model.WeatherbitAPIResponse;
import com.weather.app.repository.WeatherRepository;

class WeatherServiceTest {

    @InjectMocks
    private WeatherService weatherService;

    @Mock
    private WeatherRepository weatherRepository;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetWeather_validPostalCode() {
        String postalCode = "12345";
        String userName = "testUser";
        String apiUrl = "http://api.weatherbit.io?postal_code=12345&country=US&key=API_KEY";

        // Mock API response
        String mockJsonResponse = "{\"data\":[{\"temp\":25.3,\"rh\":60,\"wind_dir\":180,\"weather\":{\"description\":\"Clear\"}}]}";
        when(restTemplate.getForObject(any(String.class), eq(String.class))).thenReturn(mockJsonResponse);
 
        WeatherResponse response = weatherService.getWeather(postalCode, userName);

        assertNotNull(response);
        assertEquals(postalCode, response.getPostalCode());
        assertEquals(userName, response.getUserName());
        assertEquals(25.3, response.getTemperature());
        assertEquals(60, response.getHumidity());
        assertEquals("Clear", response.getDescription());
    }

    @Test
    void testGetWeather_invalidPostalCode() {
        String postalCode = "123";
        String userName = "testUser";

        WeatherResponse response = weatherService.getWeather(postalCode, userName);

        assertNotNull(response);
        assertNotNull(response.getErrorResponse());
        assertEquals("400", response.getErrorResponse().getErrorCode());
        assertEquals("Invalid postal code", response.getErrorResponse().getErrorMessage());
    }

    @Test
    void testGetHistory_withPostalCode() {
        String postalCode = "12345";

        List<WeatherHistory> mockHistories = new ArrayList<>();
        WeatherHistory history = new WeatherHistory();
        history.setPostalCode(postalCode);
        history.setUserName("testUser");
        history.setTemperature(25.3);
        history.setHumidity(60);
        history.setWeatherCondition("Clear");
        mockHistories.add(history);

        when(weatherRepository.findByPostalCode(postalCode)).thenReturn(mockHistories);

        List<WeatherHistoryResponse> responses = weatherService.getHistory(postalCode, null);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(postalCode, responses.get(0).getPostalCode());
    }

    @Test
    void testGetHistory_withUserName() {
        String userName = "testUser";

        List<WeatherHistory> mockHistories = new ArrayList<>();
        WeatherHistory history = new WeatherHistory();
        history.setPostalCode("12345");
        history.setUserName(userName);
        history.setTemperature(25.3);
        history.setHumidity(60);
        history.setWeatherCondition("Clear");
        mockHistories.add(history);

        when(weatherRepository.findByUserName(userName)).thenReturn(mockHistories);

        List<WeatherHistoryResponse> responses = weatherService.getHistory(null, userName);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(userName, responses.get(0).getUserName());
    }

    @Test
    void testGetHistory_noParametersProvided() {
        List<WeatherHistoryResponse> responses = weatherService.getHistory(null, null);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertNotNull(responses.get(0).getErrorResponse());
        assertEquals("400", responses.get(0).getErrorResponse().getErrorCode());
        assertEquals("Either postalCode or user must be provided.", responses.get(0).getErrorResponse().getErrorMessage());
    }

    @Test
    void testPersistWeatherHistory_databaseError() {
        String postalCode = "12345";
        String userName = "testUser";

        WeatherResponse weatherResponse = new WeatherResponse();
        weatherResponse.setPostalCode(postalCode);
        weatherResponse.setUserName(userName);
        weatherResponse.setTemperature(25.3);
        weatherResponse.setHumidity(60);
        weatherResponse.setDescription("Clear");

        doThrow(new RuntimeException("Database error")).when(weatherRepository).save(any(WeatherHistory.class));

        assertDoesNotThrow(() -> weatherService.persistWeatherHistory(postalCode, userName, weatherResponse));
    }

    @Test
    void testPrepareWeatherResponse_parsesDataCorrectly() {
        String jsonResponse = "{\"data\":[{\"temp\":25.3,\"rh\":60.5,\"wind_dir\":180,\"weather\":{\"description\":\"Clear\"}}]}";

        WeatherResponse weatherResponse = new WeatherResponse();
        WeatherbitAPIResponse apiResponse = weatherService.prepareWeatherBitAPIResponse(jsonResponse);

        weatherService.prepareWeatherResponse("12345", "testUser", apiResponse, weatherResponse);

        assertEquals(25.3, weatherResponse.getTemperature());
        assertEquals(60, weatherResponse.getHumidity());
        assertEquals("Clear", weatherResponse.getDescription());
    }
}
