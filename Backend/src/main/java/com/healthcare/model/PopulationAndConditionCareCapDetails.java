package com.healthcare.model;


import lombok.Data;

import java.util.List;

@Data
public class PopulationAndConditionCareCapDetails {

     private String diagnosList;
     private String convertedDiagnosList;
     private int diagnosListCount;
     private  List<PatientDetails> patientDetailsList;

}
