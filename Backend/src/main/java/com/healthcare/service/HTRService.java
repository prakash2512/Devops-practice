package com.healthcare.service;

import com.healthcare.entity.HTRInfoEntity;
import com.healthcare.model.FilterModel;
import com.healthcare.model.HTRShortAndLongModel;
import com.healthcare.model.RequestModel;

import java.util.List;
import java.util.Map;

public interface HTRService {
    /**
     * method to get distinct status in database
     * @author sowmiyathangaraj
     */
    List<String> getDistinctStatus(RequestModel model);

    /**
     * method to get distinct providers from the database
     * @author sowmiyathangaraj
     */
    List<String> getDistinctProviders(RequestModel model);

    /**
     * method to get distinct patients details from the database
     * @author sowmiyathangaraj
     */
    List<String> getDistinctPatients(RequestModel model);

    /**
     * method to get HTR details by status
     * @author sow
     */
    Map<String, Object> getHTRDetailsByStatus(RequestModel requestModel);

    /**
     * method to get HTR average census
     * @author sowmiyathangaraj
     */
    List<HTRShortAndLongModel> getHTRAverageCensus(RequestModel requestModel);

    /**
     * method to get facility details from the database
     * @author sowmiyathangaraj
     */
    List<String> getFacilityDetails();

    FilterModel getFilterDetails(RequestModel requestModel);

    List<HTRInfoEntity>  getPatientDetails(RequestModel requestModel);
}
