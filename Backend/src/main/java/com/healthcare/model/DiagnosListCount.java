package com.healthcare.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.healthcare.entity.FileDataInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class DiagnosListCount {
    private String diagnosList;
    List<ConditionCareGaptCount> conditionCareGaptCountList;
    List<FileDataInfo> patientDetailsList;

    public void addConditionCareGapCountList(List<String> stringList){
        this.conditionCareGaptCountList = new ArrayList<>();
        Map<String, Integer> countMap = new HashMap<>();
        for(String condition:stringList){
            countMap.put(condition, countMap.getOrDefault(condition, 0) + 1);
        };

        for (String countKey : countMap.keySet()) {
            int count = countMap.get(countKey);
            ConditionCareGaptCount conditionCareGaptCount;
            String diagnosis = countKey.isEmpty() ? "0" : countKey;
            conditionCareGaptCount = new ConditionCareGaptCount(diagnosis, count);
            this.conditionCareGaptCountList.add(conditionCareGaptCount);
        }
    }
}
