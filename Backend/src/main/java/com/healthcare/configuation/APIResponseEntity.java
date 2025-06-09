package com.healthcare.configuation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class APIResponseEntity<T>  implements Serializable {

    private Status status;
    private String message;
    private T response;

    public enum Status
    {
        SUCCESS, FAILED, EXCEPTION, USER_DEFINED_ERROR
    }
}
