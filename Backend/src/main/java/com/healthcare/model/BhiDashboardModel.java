package com.healthcare.model;


import com.healthcare.entity.HtrShortAndLongData;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class BhiDashboardModel {

    private Map<String,Integer> dxCounts;
    private Map<String,Integer> dxPercentage;
    private int totalPatients;
    private int schizophreniaCount;
    private int otherDx;
    List<HtrShortAndLongData> shortAndLongData;
}
