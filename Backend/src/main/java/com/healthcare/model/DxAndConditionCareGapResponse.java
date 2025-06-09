package com.healthcare.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DxAndConditionCareGapResponse {
    List<PopulationAndConditionCareCapDetails> dxDetails;
    List<ConditionCareGapCount> conditionCareGapDetails;

    List<PopulationAndConditionCareCapDetails> dxDetails2;
    List<ConditionCareGapCount> conditionCareGapDetails2;

    List<String> patientConditionList1;
    List<String> patientConditionList2;

}
