package com.healthcare.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class ImmunizationResponse {

    private String immunization;
    List<ImmunizationCountModel> immunizationCountModelList;

    public void addImmunizationCountModelList(ImmunizationCountModel immunizationCountModel){
        this.immunizationCountModelList = new ArrayList<>();
        this.immunizationCountModelList.add(immunizationCountModel);
    }
}
