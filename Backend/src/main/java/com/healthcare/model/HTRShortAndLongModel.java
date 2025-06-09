package com.healthcare.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HTRShortAndLongModel {

    private String longAverage;
    private String shortAverage;
    private String longNationAverage;
    private String shortNationAverage;
    private String longStateAverage;
    private String shortStateAverage;
    private int adk;
}
