package com.healthcare.entity;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@Data
@FieldNameConstants
@Document("bHIInfoEntity")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BHIInfoEntity {

    private LocalDate dos;
    private  LocalDate fileDate;
    private String month;
    private String serviceId;
    private String anNo;
    private String patientName;
    private String dob;
    private int patientAge;
    private String patientGender;
    private String facility;
    private String template;
    private String loginBy;
    private String signedAt;
    private String signedBy;
    private String serviceDiagnosis;
    private String cptCode;
    private String startTime;
    private String endTime;
    private String billingDate;
    private String time;
    private String appropriateDiagnosis;
    private String admittedWithSchizophrenia;
    private String show_in_hospital_records;
    private String was_interdisciplinary_meeting;
    private String comprehensive_care_plan;
    private String schizophrenia_diagnosed_in_facility;
    private String psychiatrist_consulted_and_confirmed;
    private String was_comprehensive_evaluation;
    private String was_interdisciplinary_care_plan;
    private String was_comprehensive_care_plan;
    private int first_table_switch;
    private String medication1;
    private String indication1;
    private String med1_appr;
    private String med1_inAppr;
    private String medication2;
    private String indication2;
    private String med2_appr;
    private String medication3;
    private String indication3;
    private String med3_appr;
    private String med3_inAppr;
    private String medication4;
    private String indication4;
    private String med4_appr;
    private String med4_inAppr;
    private String med5_appr;
    private String second_table_switch;
    private String type1;
    private String date_completed1;
    private int score1;
    private String type2;
    private String date_completed2;
    private int score2;
    private String fall_first;
    private String fall_second;
    private String fall_third;
    private String falls_month;
    private String recorded_fall;
    private String type_injury;
    private String consultation_history;
    private String health_maintenance;
    private String labs_new;
    private String nurses_notes;
    private String resident_hospitalized;
    private String hospitalized_diagnoses;
    private String er_visit;
    private String er_visit_diagnoses;
    private String careGaps;
    private String additionalNotes;
    private String has_pgx_review;
    private String coordinatorName;
    private String cpt_codes;
    private String checkBox2;
    private String co_signal_label;


    private String psychConsult;
    private String shortDx;
    private String gdr;

    private String programType;
    private  int year;

}
