package com.workintech.spring17challenge.exceptions;

public class ApiErrorResponse {
    private Integer status;
    private String message;
    private Long timestamp;

    public ApiErrorResponse(Integer status, String message, Long timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }

    public Integer getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Long getTimestamp() {
        return timestamp;
    }
}