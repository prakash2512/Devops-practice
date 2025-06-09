package com.healthcare.entity;


import com.healthcare.domain.Audit;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@FieldNameConstants
@Document()
public class FacilityData extends Audit {

    @Id
    private String id;
    private String facilityName;
    private String state;
    private String activeStatus;
}
