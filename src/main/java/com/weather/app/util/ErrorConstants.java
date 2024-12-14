package com.weather.app.util;

/**
 * Constants for error codes and messages.
 */
public class ErrorConstants {
    public static final String INTERNAL_SERVER_ERROR_CODE = "500";
    public static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal server error occurred";
    public static final String BAD_REQUEST_ERROR_CODE = "400";
    public static final String BAD_REQUEST_ERROR_MESSAGE = "Invalid request parameters";
    public static final String NO_RESULTS_ERROR_CODE = "204";
    public static final String NO_RESULTS_ERROR_MESSAGE = "No valid history found. Returning error response";
}
