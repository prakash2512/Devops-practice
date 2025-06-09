package com.healthcare.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.healthcare.domain.Audit;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalTime;
import java.util.Date;

@Data
@FieldNameConstants
@Document()
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HTRInfoEntity extends Audit {

    @Id
    private String id;
    private String excelId;
    private Date reportDate;
    private Enum programType;
    private String facility;
    private String state;
    private String firstName;
    private String lastName;
    private String patientName;
    private String gender;
    private Date transferDate;
    private String stayStatus;
    private String advanceDirective;
    private String provider;
    private int lengthOfStay;
    private String transferByCategory;
    private String transferByDisposition;
    private String transferByStayStatus;
    private String transferByPayerGroup;
    private String status;
    private String dateOfBirth;
    private String dateOfRecentAdmission;
    private Date hospitalizationDate;
    private LocalTime transferTime;
    private String hospitalName;
    private String report;
    private boolean RVLStudy;
    private String notes;
    private String diagnosis;
    private String categories;
    private int clinicalRiskIndicator;
    private String unit;
    private String room;
    private String month;
    private int year;
    private int age;

    private String cardioPulmonary;
    private String cardioProgram;


}
