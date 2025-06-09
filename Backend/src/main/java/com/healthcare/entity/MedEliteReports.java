package com.healthcare.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@FieldNameConstants
@Document()
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MedEliteReports {
    @Id
    private String id;
    private String dos;
    private String patientName;
    private String dob;
    private int patientAge;
    private String patientGender;
    private String facilityName;
    private String template;
    private String loginBy;
    private String signedAt;
    private String signedBy;
    private String serviceDiagnosis;
    private String cptCode;
    private String billingDate;
    private String previousVisitDate;
    private String ccphmVisit;
    private String diagnosis;
    private String diagnosList;
    private String carePlan;
    private String careGaps;
    private String patientConditionDiag1;
    private String diagnosList_2;
    private String carePlan2;
    private String caregaps2;
    private String patientConditionDiag2;
    private Date reportDate;
    private String programType;
    private String month;
    private int year;

    private String pneumococcal;
    private String influenza;
    private String prevnar;
    private String covidVaccine1;
    private String covidVaccine2;
    private String covidBooster;


    private String fallRiskAssessment;
    private String fallRiskCarePlan;
    private String skinRiskAssessment;
    private String skinRiskCategory;
    private String skinRiskStore;
    private String skinRiskCarePlan;
    private String catheter;
    private String catheterType;
    private String catheterCarePlan;
    private String catheterIndication;
    private String advanceDirective;
    private String advanceDirectivesTypes;
    private String advCarePlan;
    private String advDoctorsOrder;




//
//    public MedEliteReports(String doS, String patientname, String dateofbirth, int patientage,
//                           String patientgender, String referringfacility, String template, String loginby, String signedat,
//                           String signedby, String servicediagnosis,String cptCode, String billingDate, String prevVisitDate,
//                           String ccphmVisit, String diagnosis, String diagnosList, String careplan, String caregaps,
//                           String patientConditionDiag1, String diagnosList2, String careplan2, String caregaps2, String patientConditionDiag2, String month, int year, String programType) {
//        this.dos = doS;
//        this.patientName = patientname;
//        this.dob = dateofbirth;
//        this.patientAge = patientage;
//        this.patientGender = patientgender;
//        this.facilityName = referringfacility;
//        this.template = template;
//        this.loginBy = loginby;
//        this.signedAt = signedat;
//        this.signedBy = signedby;
//        this.serviceDiagnosis = servicediagnosis;
//        this.cptCode = cptCode;
//        this.billingDate = billingDate;
//        this.previousVisitDate = prevVisitDate;
//        this.ccphmVisit = ccphmVisit;
//        this.diagnosis = diagnosis;
//        this.diagnosList = diagnosList;
//        this.carePlan = careplan;
//        this.careGaps = caregaps;
//        this.patientConditionDiag1 = patientConditionDiag1;
//        this.diagnosList_2 = diagnosList2;
//        this.carePlan2 = careplan2;
//        this.caregaps2 = caregaps2;
//        this.patientConditionDiag2 = patientConditionDiag2;
//        this.month = month;
//        this.year = year;
//        this.programType = programType;
//
//
//    }
}
