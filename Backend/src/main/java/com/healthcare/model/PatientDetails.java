package com.healthcare.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class PatientDetails {
    private String patientName;
    private String facilityName;
    private String diagnosList;
    private String diagnosList2;
    private String careGaps;
    private String careGaps2;
    private String carePlan;
    private String conditionCaregap;
    private String conditionCaregap2;
    private String pneumococcal;
    private String influenza;
    private String prevnar;
    private String covidVaccine1;
    private String covidVaccine2;
    private String covidBooster;
}
