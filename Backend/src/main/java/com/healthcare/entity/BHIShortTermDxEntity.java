package com.healthcare.entity;


import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@FieldNameConstants
@Document()
public class BHIShortTermDxEntity {

    private String dx;
    private String shortDx;
}
