package com.aurelius.tech.mybakery.dto;

import java.time.LocalDateTime;

/**
 * Standard API response format for all endpoints.
 *
 * @param <T> the type of data in the response
 */
public class ApiResponse<T> {

    private boolean success;
    private T data;
    private String message;
    private String timestamp;

    /**
     * Default constructor.
     */
    public ApiResponse() {
        this.timestamp = LocalDateTime.now().toString();
    }

    /**
     * Constructor for success response with data.
     *
     * @param data the response data
     * @param message the success message
     */
    public ApiResponse(T data, String message) {
        this.success = true;
        this.data = data;
        this.message = message;
        this.timestamp = LocalDateTime.now().toString();
    }

    /**
     * Constructor for success response without data.
     *
     * @param message the success message
     */
    public ApiResponse(String message) {
        this.success = true;
        this.message = message;
        this.timestamp = LocalDateTime.now().toString();
    }

    /**
     * Static factory method for creating a success response with data.
     *
     * @param data the response data
     * @param message the success message
     * @param <T> the type of data
     * @return a success response
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(data, message);
    }

    /**
     * Static factory method for creating a success response without data.
     *
     * @param message the success message
     * @param <T> the type of data
     * @return a success response
     */
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(message);
    }

    /**
     * Static factory method for creating an error response.
     *
     * @param message the error message
     * @param <T> the type of data
     * @return an error response
     */
    public static <T> ApiResponse<T> error(String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }

    // Getters and Setters

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}