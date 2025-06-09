package com.healthcare.model;


import com.healthcare.entity.BHIInfoEntity;
import com.healthcare.entity.FileDataInfo;
import com.healthcare.entity.HTRInfoEntity;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class DynamicProgramFilter {

    private List<BHIInfoEntity> bhiList;
    private List<FileDataInfo> ccmList;
    private List<HTRInfoEntity> htrList;
    private Map<String, Map<String, Integer>> htrDispositionCount;

    public void calculateHtrDispositionCount(List<HTRInfoEntity> htrList) {

        this.htrDispositionCount = new HashMap<>();

        Map<String, Map<String, Integer>> dispositionCount = new HashMap<>();

        for (HTRInfoEntity htrInfoEntity : htrList) {
            dispositionCount.putIfAbsent(htrInfoEntity.getMonth(), new HashMap<>());
            Map<String, Integer> countMap = dispositionCount.get(htrInfoEntity.getMonth());
            countMap.put(htrInfoEntity.getTransferByDisposition(), countMap.getOrDefault(htrInfoEntity.getTransferByDisposition(), 0) + 1);
        }
        this.setHtrDispositionCount(dispositionCount);

    }
}
