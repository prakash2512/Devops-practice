package com.healthcare.entity;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.healthcare.domain.Audit;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.Date;

@Data
@FieldNameConstants
@Document()
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HtrShortAndLongData extends Audit {

    @Id
    private String id;
    private String excelId;
    private String month;
    private String facility;
    private String state;
    private String averageCensus;
    private String shortNationAverage;
    private String shortStateAverage;
    private String shortAverage;
    private String longNationAverage;
    private String longStateAverage;
    private String longAverage;
    private String totalNoOfAdmits;
    private int adk;
    private int year;
}
