package com.healthcare.entity;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.healthcare.domain.Audit;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;

@Data
@FieldNameConstants
@Document()
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileDataInfo extends Audit {
    @Id
    private String id;
    private String excelId;
    private Date reportDate;
    private Enum programType;
    private String patientName;
    private int patientAge;
    private String dob;
    private String facilityName;
    private String patientGender;
    private String pneumococcal;
    private String influenza;
    private String prevnar;
    private String covidVaccine1;
    private String covidVaccine2;
    private String covidBooster;
    private String diagnosList;
    private String diagnosList2;
    private String careGaps;
    private String fallRiskAssessment; //
    private String fallRiskCarePlan;//
    private String skinRiskAssessment;//
    private String skinRiskCategory;
    private String skinRiskStore;
    private String skinRiskCarePlan; //
    private String catheter;//
    private String catheterType;
    private String catheterCarePlan; //
    private String catheterIndication;//
    private String advanceDirective;//
    private String advanceDirectivesTypes;
    private String advCarePlan;//
    private String advDoctorsOrder;//
    private String residentHospitalized;
    private String hospitalizedDiagnosis;
    private String erVisit;
    private boolean isCareGapsLastReviewDiag1;
    private String patientConditionDiag1;
    private String patientConditionDiag2;
    private String careGaps2;
    private String Dos;
    private String month;
    private int year;
    private  String physician;
    private String hospitalizedDate;
    private LocalDate hospitalizedFromDate;
    private LocalDate hospitalizedEndDate;
    private int daysOfStay;


    private Boolean showCareGaps1;
    private Boolean showCareGaps2;


    public String getFieldValue(String field) {
        return switch (field) {
            case "influenza" -> getInfluenza();
            case "prevnar" -> getPrevnar();
            case "pneumococcal" -> getPneumococcal();
            case "covidVaccine1" -> getCovidVaccine1();
            case "covidVaccine2" -> getCovidVaccine2();
            case "covidBooster" -> getCovidBooster();
            default -> null;
        };
    }
}
