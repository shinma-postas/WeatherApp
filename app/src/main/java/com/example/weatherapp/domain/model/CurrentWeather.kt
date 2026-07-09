package com.example.weatherapp.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CurrentWeather(
    val temperature_2m: Double,
    val weather_code: Int
)