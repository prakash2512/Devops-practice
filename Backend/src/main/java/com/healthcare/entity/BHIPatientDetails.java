package com.healthcare.entity;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@FieldNameConstants
@Document()
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BHIPatientDetails {
   private  List<BHIInfoEntity> bhiInfoEntityList;
    private Map<String,Integer> psychConsultCount;
    private Map<String,Integer> fallGxCount;
    private Map<String,Integer> gdrCount;
    private Set<String>patientNames;
    private Set<String>dx;

    private int totalPatients;
    private int schizophreniaCount;
    private int otherDx;
}
