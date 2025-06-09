package com.healthcare.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.healthcare.domain.Audit;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@FieldNameConstants
@Document(collection = "programsInfo")
public class ProgramsInfoEntity extends Audit {

    @Id
    private String id;
    private String programName;
    private String programColumnName;
    private String columnDataType;
    @JsonProperty("isRequired")
    private boolean isRequired;

}
