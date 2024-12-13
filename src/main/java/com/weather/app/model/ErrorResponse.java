package com.weather.app.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ErrorResponse {
    private String errorCode;
    private String errorMessage;
}
