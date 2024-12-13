package com.weather.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.weather.app.model.WeatherHistory;

import java.util.List;

@Repository
public interface WeatherRepository extends JpaRepository<WeatherHistory, Long> {
    List<WeatherHistory> findByPostalCode(String postalCode);

    List<WeatherHistory> findByUserName(String userName);
}