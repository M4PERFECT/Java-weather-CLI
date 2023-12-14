import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class WeatherApp {

    private static WeatherData parseCurrentWeatherData(String json) {
        WeatherData weather = new WeatherData();

        try {
            JSONObject jsonObject = new JSONObject(json);

            JSONObject mainObject = jsonObject.getJSONObject("main");
            double temperatureKelvin = mainObject.getDouble("temp");
            double temperatureCelsius = temperatureKelvin - 273.15;
            weather.setTemperature(temperatureCelsius);

            JSONArray weatherArray = jsonObject.getJSONArray("weather");
            if (weatherArray.length() > 0) {
                JSONObject weatherObject = weatherArray.getJSONObject(0);
                String description = weatherObject.getString("description");
                weather.setDescription(description);
            }

            int humidity = mainObject.getInt("humidity");
            weather.setHumidity(humidity);

            JSONObject windObject = jsonObject.getJSONObject("wind");
            double windSpeed = windObject.getDouble("speed");
            weather.setWindSpeed(windSpeed);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return weather;
    }

    private static List<WeatherData> parseForecastData(String json) {
        List<WeatherData> forecast = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(json);

            JSONArray forecastArray = jsonObject.getJSONArray("list");
            for (int i = 0; i < forecastArray.length(); i++) {
                JSONObject forecastObject = forecastArray.getJSONObject(i);
                WeatherData weather = new WeatherData();

                JSONObject mainObject = forecastObject.getJSONObject("main");
                double temperatureKelvin = mainObject.getDouble("temp");
                double temperatureCelsius = temperatureKelvin - 273.15;
                weather.setTemperature(temperatureCelsius);

                JSONArray weatherArray = forecastObject.getJSONArray("weather");
                if (weatherArray.length() > 0) {
                    JSONObject weatherObject = weatherArray.getJSONObject(0);
                    String description = weatherObject.getString("description");
                    weather.setDescription(description);
                }

                forecast.add(weather);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return forecast;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("--------------------------------------------------------");
        System.out.println("Welcome to the Weather Forecast App!");

        while (true) {
            System.out.println("--------------------------------------------------------");
            System.out.print("Please enter a location (city name) or type 'quit' to exit: ");
            String location = scanner.nextLine();

            if (location.equalsIgnoreCase("quit")) {
                System.out.println("Thank you for using the Weather Forecast App. Goodbye!");
                break;
            }

            String apiKey = "c56db9a857b7ce323bdbc40156c05260"; 

            String currentWeatherApiUrl = "http://api.openweathermap.org/data/2.5/weather?q=" + location + "&appid=" + apiKey;
            String forecastApiUrl = "http://api.openweathermap.org/data/2.5/forecast?q=" + location + "&appid=" + apiKey;

            try {
                URI uri = new URI(currentWeatherApiUrl);
                HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
                BufferedReader apiReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = apiReader.readLine()) != null) {
                    response.append(line);
                }
                apiReader.close();

                String currentWeatherData = response.toString();

                WeatherData currentWeather = parseCurrentWeatherData(currentWeatherData);

                System.out.println("Here's the current weather information for " + location + ":");
                System.out.println("Temperature: " + String.format("%.2f", currentWeather.getTemperature()) + " °C");
                System.out.println("Description: " + currentWeather.getDescription());
                System.out.println("Humidity: " + currentWeather.getHumidity() + "%");
                System.out.println("Wind Speed: " + currentWeather.getWindSpeed() + " m/s");

                uri = new URI(forecastApiUrl);
                connection = (HttpURLConnection) uri.toURL().openConnection();
                apiReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                response = new StringBuilder();
                while ((line = apiReader.readLine()) != null) {
                    response.append(line);
                }
                apiReader.close();

                String forecastData = response.toString();
                List<WeatherData> forecast = parseForecastData(forecastData);

                System.out.println("\nHere's the weather forecast for the next few hours in " + location + ":");
                for (int i = 0; i < 5 && i < forecast.size(); i++) {
                    WeatherData weather = forecast.get(i); 
                    System.out.println("Temperature: " + String.format("%.2f", weather.getTemperature()) + " °C");
                    System.out.println("Description: " + weather.getDescription());
                    System.out.println("------------");
                }

                connection.disconnect();
            } catch (IOException | URISyntaxException e) {
                System.out.println("An error occurred while fetching the weather data.");
                e.printStackTrace();
            }
        }

        scanner.close();
    }
}

class WeatherData {
    private double temperature;
    private String description;
    private int humidity;
    private double windSpeed;

    public double getTemperature() {
        return temperature;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
