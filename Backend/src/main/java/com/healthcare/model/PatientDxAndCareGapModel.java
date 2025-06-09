package com.healthcare.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.Document;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientDxAndCareGapModel {
    List<DiagnosListCount> conditionCareGapCount;
    List<Document> patientPopulationCount;
}
