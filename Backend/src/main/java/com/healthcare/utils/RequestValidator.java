package com.healthcare.utils;

import com.healthcare.configuation.APIResponseEntity;
import com.healthcare.model.RequestModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class RequestValidator {
    public static ResponseEntity<APIResponseEntity<?>> validateRequest(RequestModel model) {
        StringBuilder errorMessage = new StringBuilder();

        if (model.getFacilityName() == null || model.getFacilityName().isEmpty()) {
            errorMessage.append("FacilityName is null or empty. ");
        }
        if (model.getMonth() == null || model.getMonth().isEmpty()) {
            errorMessage.append("Month is null or empty. ");
        }
        if (model.getYear() == 0) {
            errorMessage.append("Year is 0. ");
        }
        if (model.getProgram() == null || model.getProgram().isEmpty()) {
            errorMessage.append("ProgramType is null or empty. ");
        }

        if (!errorMessage.isEmpty()) {
            return ResponseEntity.status(StatusCode.NOT_FOUND)
                    .body(new APIResponseEntity<>(
                            APIResponseEntity.Status.FAILED,
                            errorMessage.toString().trim(),
                            "Failed to get data from the DB"
                    ));
        }

        return null; // means validation passed
    }
}
