package com.healthcare.model;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data // Lombok to reduce boilerplate code
@AllArgsConstructor
public class TransferDetailsModel {

    private String patientName;
    private String gender;
    private String diagnosis;
    private String stayStatus;
    private String transferDateAndTime;
 }
