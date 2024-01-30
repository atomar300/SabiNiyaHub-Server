package com.ashish.sabiniyahub.exception;
import lombok.Data;

@Data
public class APIError {

    private boolean success = false;
    private String message;

    public APIError(String message){
        this.message = message;
    }

}
