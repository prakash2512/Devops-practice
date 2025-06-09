package com.healthcare.service;



import com.healthcare.configuation.APIResponseEntity;
import com.healthcare.entity.FileDataInfo;
import com.healthcare.entity.MedEliteReports;
import com.healthcare.model.CCMResponseModel;
import com.healthcare.model.PatientDetails;
import com.healthcare.model.RequestModel;
import org.bson.Document;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface ICCMService {

    /**
     * method get ccm details from database
     * @author sowmiyathangaraj
     * @param model
     */

    List<CCMResponseModel> getCCMPopulationDetails(RequestModel model);

    /**
     * method to get diagnosis counts from the database
     * @author sowmiyathangaraj
     * @param model
     */
    Map<String, Integer> getDiagnosisCounts(RequestModel model);

    /**
     * method to get patient condition list
     * @author sowmiyathangaraj
     * @param model
     */
    List<String> getPatientConditionList(RequestModel model);

    /**
     * method to get immunization details from the datatbase
     * @author sowmiyathangaraj
     * @param model
     */
    Map<String, Map<String, Integer>> getImmunizationDetails(RequestModel model);


    /**
     * method to get single immunization details from database
     * @author sowmiyathangaraj
     * @param model
     */
    List<CCMResponseModel> singleImmunizationDetails(RequestModel model);


    /**
     * method to get assessment details from database
     * @author sowmiyathanagaraj
     * @param model
     */
    Map<String, Map<String, Integer>> getAssessmentDetails(RequestModel model);


    /**
     * method to get single assessment details from database
     * @author sowmiyathangaraj
     * @param model
     * @return
     */
    List<CCMResponseModel> singleAssessmentDetails(RequestModel model);


    /**
     * method to get patient information by patient condition
     * @author sowmiyathangaraj
     * @param model
     */
    List<CCMResponseModel> getPatientInfoByPatientCondition(RequestModel model);

    List<PatientDetails> getPatientDetails(RequestModel model);

    List<Document> getDiagnosisListCount(RequestModel model);

    ResponseEntity<APIResponseEntity<?>> getConditionCareGapCount(RequestModel requestModal);

    ResponseEntity<APIResponseEntity<?>> getPatientFilter(RequestModel requestModal);

    ResponseEntity<APIResponseEntity<?>> getPopulationAndConditionCount(RequestModel requestModal);

    ResponseEntity<APIResponseEntity<?>> getPatientNames(RequestModel requestModal);

    ResponseEntity<APIResponseEntity<?>> getProgramFilter(RequestModel model);

    ResponseEntity<APIResponseEntity<?>> editProgramFilter(RequestModel requestModal);

    ResponseEntity<APIResponseEntity<?>> getYears();

    ResponseEntity<APIResponseEntity<?>> cmmDetails(RequestModel requestModal);
}
