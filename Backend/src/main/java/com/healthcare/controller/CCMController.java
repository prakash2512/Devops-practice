package com.healthcare.controller;
import com.healthcare.configuation.APIResponseEntity;
import com.healthcare.configuation.Messages;
import com.healthcare.model.CCMResponseModel;
import com.healthcare.model.PatientDetails;
import com.healthcare.model.RequestModel;
import com.healthcare.service.ICCMService;
import com.healthcare.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ccm")
public class CCMController {

    @Autowired
    ICCMService service;

    /**
     * method to get ccm details
     * @author sowmiyathangaraj
     * @param model
     * @return responseModels
     */
    @PostMapping(value = "/getPopulationDetails",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<APIResponseEntity<?>> getCCMPopulationDetails(@RequestBody RequestModel model){
        try {
            List<CCMResponseModel> responseModels = service.getCCMPopulationDetails(model);
            if (!responseModels.isEmpty()) {
                return ResponseEntity.status(StatusCode.OK)
                        .body(new APIResponseEntity<>(APIResponseEntity.Status.SUCCESS, Messages.SUCCESS,
                                responseModels));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(StatusCode.EXCEPTION)
                    .body(new APIResponseEntity<>(APIResponseEntity.Status.FAILED, Messages.FAILED,
                            e.getLocalizedMessage()));
        }
        return null;

    }

    /**
     * method get diagnosis count from the database
     * @author sowmiyathangaraj
     * @param model
     * @return diagnosisCount
     */
    @PostMapping(value = "/getDiagnosisCounts",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<APIResponseEntity<?>> getDiagnosisCounts(@RequestBody RequestModel model){
        try {
            Map<String, Integer> diagnosisCount = service.getDiagnosisCounts(model);
           // List<Document> result =service.getDiagnosisListCount(model);

            if (!diagnosisCount.isEmpty()) {
                return ResponseEntity.status(StatusCode.OK).body(new APIResponseEntity<>(APIResponseEntity.Status.SUCCESS, Messages.SUCCESS, diagnosisCount));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(StatusCode.EXCEPTION)
                    .body(new APIResponseEntity<>(APIResponseEntity.Status.FAILED, Messages.FAILED,
                            e.getLocalizedMessage()));
        }
        return null;
    }

    /**
     * method to get patient condition list
     * @author sowmiyathangaraj
     * @return patientConditionList
     */
    @PostMapping(value = "/getPatientConditionList",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<APIResponseEntity<?>> getPatientConditionList(@RequestBody RequestModel model){
        try {
            List<String> patientConditionList = service.getPatientConditionList(model);
            if (!patientConditionList.isEmpty()) {
                return ResponseEntity.status(StatusCode.OK).body(new APIResponseEntity<>(APIResponseEntity.Status.SUCCESS, Messages.SUCCESS, patientConditionList));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(StatusCode.EXCEPTION)
                    .body(new APIResponseEntity<>(APIResponseEntity.Status.FAILED, Messages.FAILED,
                            e.getLocalizedMessage()));
        }
        return null;
    }

    /**
     * method to get immunization details from database
     * @author sowmiyathangaraj
     * @param model
     * @return response
     */
    @PostMapping(value = "/getImmunizationDetails",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<APIResponseEntity<?>> getImmunizationDetails(@RequestBody RequestModel model){
        try {
            Map<String, Map<String, Integer>> response = service.getImmunizationDetails(model);
            if (!response.isEmpty()) {
                return ResponseEntity.status(StatusCode.OK)
                        .body(new APIResponseEntity<>(APIResponseEntity.Status.SUCCESS, Messages.SUCCESS, response));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(StatusCode.EXCEPTION)
                    .body(new APIResponseEntity<>(APIResponseEntity.Status.FAILED, Messages.FAILED,
                            e.getLocalizedMessage()));
        }
        return null;
    }

    /**
     * method to get single immunization
     * @param model
     * @return
     */
    @PostMapping(value = "/singleImmunizationDetails",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<APIResponseEntity<?>> singleImmunizationDetails(@RequestBody RequestModel model){
        try {
            List<CCMResponseModel> singleImmunizationDetailsResponse = service.singleImmunizationDetails(model);
             if(!singleImmunizationDetailsResponse.isEmpty()){
                 return ResponseEntity.status(StatusCode.OK)
                         .body(new APIResponseEntity<>(APIResponseEntity.Status.SUCCESS, Messages.SUCCESS, singleImmunizationDetailsResponse));
             }
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(StatusCode.EXCEPTION)
                    .body(new APIResponseEntity<>(APIResponseEntity.Status.FAILED, Messages.FAILED,
                            e.getLocalizedMessage()));
        }
        return null;
    }
    /**
     * method to get assessment details from database
     * @author sowmiyathangaraj
     * @param model
     * @return
     */

    @PostMapping(value = "/getAssessmentDetails",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<APIResponseEntity<?>> getAssessmentDetails(@RequestBody RequestModel model){
        try {
            Map<String, Map<String, Integer>> getAssessmentDetails = service.getAssessmentDetails(model);
            if(!getAssessmentDetails.isEmpty()){
                return ResponseEntity.status(StatusCode.OK)
                        .body(new APIResponseEntity<>(APIResponseEntity.Status.SUCCESS, Messages.SUCCESS, getAssessmentDetails));
            }

        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(StatusCode.EXCEPTION)
                    .body(new APIResponseEntity<>(APIResponseEntity.Status.FAILED, Messages.FAILED,
                            e.getLocalizedMessage()));
        }
        return null;
    }

    /**
     * method to get single assessment from database
     * @author sowmiyathangaraj
     * @param  model
     * @return getAssessmentDetails
     */
    @PostMapping(value = "/singleAssessmentDetails",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<APIResponseEntity<?>> singleAssessmentDetails(@RequestBody RequestModel model){
        try {
            List<CCMResponseModel> singleAssessmentDetails = service.singleAssessmentDetails(model);
            if(!singleAssessmentDetails.isEmpty()){
                return ResponseEntity.status(StatusCode.OK)
                        .body(new APIResponseEntity<>(APIResponseEntity.Status.SUCCESS, Messages.SUCCESS, singleAssessmentDetails));
            }

        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(StatusCode.EXCEPTION)
                    .body(new APIResponseEntity<>(APIResponseEntity.Status.FAILED, Messages.FAILED,
                            e.getLocalizedMessage()));
        }
        return null;
    }

    /**
     * method to get patient information by patient condition
     * @author sowmiyathangaraj
     * @param model
     * @return
     */
    @PostMapping(value = "/getPatientInfo",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<APIResponseEntity<?>> getPatientInfoByPatientCondition(@RequestBody RequestModel model){
        try {
            List<CCMResponseModel> getPatientInfoByPatientCondition = service.getPatientInfoByPatientCondition(model);
            if(!getPatientInfoByPatientCondition.isEmpty()){
                return ResponseEntity.status(StatusCode.OK)
                        .body(new APIResponseEntity<>(APIResponseEntity.Status.SUCCESS, Messages.SUCCESS, getPatientInfoByPatientCondition));
            }

        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(StatusCode.EXCEPTION)
                    .body(new APIResponseEntity<>(APIResponseEntity.Status.FAILED, Messages.FAILED,
                            e.getLocalizedMessage()));
        }
        return null;
    }

    /**
     * method to get patient information by patient condition
     * @author yogaraj
     * @param model
     * @return
     */
    @PostMapping(value = "/get/patient/details",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<APIResponseEntity<?>> getPatientDetails(@RequestBody RequestModel model){

        if(model.getYear() == 0 ||   model.getMonth() == null || model.getMonth().isEmpty() || model.getFacilityName() == null || model.getFacilityName().isEmpty()){
            return ResponseEntity.status(400)
                    .body(new APIResponseEntity<>(APIResponseEntity.Status.FAILED, "one of the fields year,month or facilityName or programType is null or empty", null));
        }
        try {
            List<PatientDetails> getPatientInfo = service.getPatientDetails(model);
            if(!getPatientInfo.isEmpty()){
                return ResponseEntity.status(StatusCode.OK)
                        .body(new APIResponseEntity<>(APIResponseEntity.Status.SUCCESS, Messages.SUCCESS, getPatientInfo));
            }else{
                return ResponseEntity.status(StatusCode.NOT_FOUND)
                        .body(new APIResponseEntity<>(APIResponseEntity.Status.SUCCESS, "NO DATA FOUND", getPatientInfo));
            }
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(StatusCode.EXCEPTION)
                    .body(new APIResponseEntity<>(APIResponseEntity.Status.FAILED, Messages.FAILED,
                            e.getLocalizedMessage()));
        }
    }


    /**
     * method to get condition caregap count
     * @author yogaraj
     */
    @PostMapping("/get/populationAndConditionCareGap/count")
    public ResponseEntity<APIResponseEntity<?>> getConditionCareGapCount(@RequestBody RequestModel requestModal){
        //return service.getConditionCareGapCount(requestModal);
      return service.getPopulationAndConditionCount(requestModal);
    };

    /**
     * method to get condition caregap count
     * @author yogaraj
     */
    @PostMapping("/get/patient/filter")
    public ResponseEntity<APIResponseEntity<?>> patientFilter(@RequestBody RequestModel requestModal){
        return service.getPatientFilter(requestModal);
    };


    /**
     * method to get patientNames
     * @author yogaraj
     */
    @PostMapping("/get/patientsNames")
    public ResponseEntity<APIResponseEntity<?>> getPatientNames(@RequestBody RequestModel requestModal){
        return service.getPatientNames(requestModal);
    };


    /**
     * method to get patientNames
     * @author yogaraj
     */
    @PostMapping("/patients/program/filter")
    public ResponseEntity<APIResponseEntity<?>> programFilter(@RequestBody RequestModel requestModal){
        return service.getProgramFilter(requestModal);
    };

    /**
     * method to get CCM Details
     * @author yogaraj
     */
    @PostMapping("/get/details")
    public ResponseEntity<APIResponseEntity<?>> getDetails(@RequestBody RequestModel requestModal){
        return service.cmmDetails(requestModal);
    };


    /**
     * method to get patientNames
     * @author yogaraj
     */
    @PostMapping("/edit/program/filter")
    public ResponseEntity<APIResponseEntity<?>> editProgramFilter(@RequestBody RequestModel requestModal){
        return service.editProgramFilter(requestModal);
    };

    /**
     * method to get distinct years
     * @author yogaraj
     */
    @GetMapping("/get/years")
    public ResponseEntity<APIResponseEntity<?>> getYears(){
        return service.getYears();
    };


}



