# Weather App

## Requirement
Create a service that fetches weather data based on the provided postal code.

## Tech Stack
- Java
- Spring Boot
- In-memory H2 Database
- Maven for build

## Note
The application uses [Weatherbit API](https://www.weatherbit.io/api) to fetch weather details.

## How to Use
1. Install Maven on your local system.
2. Clone this repository:
   ```bash
   git clone https://github.com/your-username/weather-app.git

## build the application
1. Navigate to the project's root directory.
2. Build the project:
3. mvn clean install

## Run the application:
1. Start it like any standard Spring Boot application.
2. Run the WeatherApplication class as a Java application.
## Configure your API token:
1. Obtain an API token from Weatherbit.
2. Add it to the application.properties file under the property: weatherbit.api.key=YOUR_API_KEY

## Out of Scope
1. This test focuses on evaluating the ability to write the Weather API.
2. Multi-environment deployment is not addressed.
3. Optional requirements are not included.

## Assumptions
1. The application currently supports only the **US**, and this value is hardcoded in the configuration.  
   - For future enhancements, the country code could be added as an API parameter.

2. User details are taken from the request parameter.  
   - For a production-ready application, user details would ideally be extracted from a token.
 
 ## API's
 1. Get the weather details: http://localhost:8080/app/weather?postalCode=10001&userName=amiya
 2. Get the history: http://localhost:8080/app/history?postalCode=10001&userName=amiya