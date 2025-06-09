package com.healthcare.entity;


import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@FieldNameConstants
@Document()
public class ConvertedDiagnosisMappingEntity {
    private String diagnosis;
    private String convertedDiagnosis;
}
