package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorDetails {
    private Integer code;
    private String message;
    public ErrorDetails (Integer code, String message){
        this.code = code;
        this.message= message;
    }
}