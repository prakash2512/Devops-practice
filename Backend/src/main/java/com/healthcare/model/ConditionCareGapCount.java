package com.healthcare.model;

import lombok.Data;

import java.util.HashMap;


@Data
public class ConditionCareGapCount {
    private String diagnosList;
    private String convertedDiagnosList;
    HashMap<String,Integer> diagnosCount;
}
