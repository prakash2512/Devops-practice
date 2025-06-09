package com.healthcare.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.mapping.Document;

@FieldNameConstants
@Document()
@Data
@AllArgsConstructor
public class StateAndFacilities {
    private String facilityName;
    private String state;
    private String coordinator;
}
