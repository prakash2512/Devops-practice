package com.healthcare.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CCMResponseModel {
    private String id;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int count;
    private String patientName;
    private String immunizationName;
    private String assessmentName;
    private String careGaps;
    private String diagnosList;
    private String patientConditionDiag1;
}
