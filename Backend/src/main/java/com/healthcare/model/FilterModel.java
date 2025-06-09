package com.healthcare.model;


import lombok.Data;

import java.util.List;

@Data
public class FilterModel {

    private List<String> statusList;
    private List<String> patientList;
    private List<String> providerList;


}
