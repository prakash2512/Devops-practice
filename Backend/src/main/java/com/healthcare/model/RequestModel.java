package com.healthcare.model;


import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class RequestModel {
    private LocalDate reportDate;
    private String facilityName;
    private List<String> patientName;
    private String immunizationName;
    private String assessmentName;
    private List<String> patientCondition;
    private String month;
    private int year;
    private String program;
    private int age;
    private int dx;
    private String htrStatus;
    private String htrProvider;
    List<String> immunizationCondition;
    private int getLastMonths;
    private String psychConsult;
    private String fallGx;
    private List<String> diagnosis;
    private String id;
    private List<String> programTypes;
    private String careGaps1;
    private String careGaps2;
    private String userName;
    private String password;
    private String residentHospitalized;
    private List<String > months;
    private String cardio;
    private String cardioPulmonary;
}
