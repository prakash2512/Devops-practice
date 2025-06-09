package com.healthcare.controller;
import com.healthcare.configuation.APIResponseEntity;
import com.healthcare.configuation.Messages;
import com.healthcare.entity.HTRInfoEntity;
import com.healthcare.model.FilterModel;
import com.healthcare.model.HTRShortAndLongModel;
import com.healthcare.model.RequestModel;
import com.healthcare.service.HTRService;
import com.healthcare.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/htr")
public class HTRController {

    @Autowired
    HTRService service;

    /**
     * method to get distinct status in database
     * @author sowmiyathangaraj
     */
    @PostMapping(value = "/getStatusDetails",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<APIResponseEntity<?>> getDistinctStatus(@RequestBody RequestModel requestModel){
        try {
            List<String> responseModels = service.getDistinctStatus(requestModel);
            return getApiResponseEntity(responseModels);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(StatusCode.EXCEPTION)
                    .body(new APIResponseEntity<>(APIResponseEntity.Status.FAILED, Messages.FAILED,
                            e.getLocalizedMessage()));
        }
    }

    /**
     * method to get distinct provider in database
     * @author sowmiyathangaraj
     */
    @PostMapping(value = "/getProviderDetails",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<APIResponseEntity<?>> getDistinctProviders(@RequestBody RequestModel requestModel){
        try {
            List<String> response = service.getDistinctProviders(requestModel);
            return getApiResponseEntity(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(StatusCode.EXCEPTION)
                    .body(new APIResponseEntity<>(APIResponseEntity.Status.FAILED, Messages.FAILED,
                            e.getLocalizedMessage()));
        }
    }

    /**
     * method to get distinct provider in database
     * @author sowmiyathangaraj
     */
    @PostMapping(value = "/getPatientDetails",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<APIResponseEntity<?>> getDistinctPatients(@RequestBody RequestModel requestModel){
        try {
            List<String> response = service.getDistinctPatients(requestModel);
            return getApiResponseEntity(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(StatusCode.EXCEPTION)
                    .body(new APIResponseEntity<>(APIResponseEntity.Status.FAILED, Messages.FAILED,
                            e.getLocalizedMessage()));
        }
    }


    /**
     * method to generate api response
     * @author sowmiyathangaraj
     */
    private ResponseEntity<APIResponseEntity<?>> getApiResponseEntity(List<String> response) {
        if (!response.isEmpty()) {
            return ResponseEntity.status(StatusCode.OK)
                    .body(new APIResponseEntity<>(APIResponseEntity.Status.SUCCESS, Messages.SUCCESS,
                            response));
        }else{
            return ResponseEntity.status(StatusCode.OK)
                    .body(new APIResponseEntity<>(APIResponseEntity.Status.FAILED, Messages.FAILED,
                            "No Record Found"));
        }
    }

    /**
     * method to get HTR details by status
     * @author sowmiyathangaraj
     */
    @PostMapping(value = "/dashboard/details",produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<APIResponseEntity<?>> getHTRDetailsByStatus(@RequestBody RequestModel requestModel){
        try {
            Map<String, Object> response = service.getHTRDetailsByStatus(requestModel);
            if(response != null){
                return ResponseEntity.status(StatusCode.OK)
                        .body(new APIResponseEntity<>(APIResponseEntity.Status.SUCCESS, Messages.SUCCESS,
                                response));
            }else{
                return ResponseEntity.status(StatusCode.OK)
                        .body(new APIResponseEntity<>(APIResponseEntity.Status.FAILED, Messages.FAILED,
                                "No Record Found"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(StatusCode.EXCEPTION)
                    .body(new APIResponseEntity<>(APIResponseEntity.Status.FAILED, Messages.FAILED,
                            e.getLocalizedMessage()));
        }
    }

    /**
     * method to get HTR average census
     * @author sowmiyathangaraj
     */
    @PostMapping(value = "/getHTRAverageCensus",produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<APIResponseEntity<?>> getHTRAverageCensus(@RequestBody RequestModel requestModel){
        try {
            List<HTRShortAndLongModel> response = service.getHTRAverageCensus(requestModel);
            if(response != null){
                return ResponseEntity.status(StatusCode.OK)
                        .body(new APIResponseEntity<>(APIResponseEntity.Status.SUCCESS, Messages.SUCCESS,
                                response));
            }else{
                return ResponseEntity.status(StatusCode.OK)
                        .body(new APIResponseEntity<>(APIResponseEntity.Status.FAILED, Messages.FAILED,
                                "No Record Found"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(StatusCode.EXCEPTION)
                    .body(new APIResponseEntity<>(APIResponseEntity.Status.FAILED, Messages.FAILED,
                            e.getLocalizedMessage()));
        }
    }



    @GetMapping(value = "/getFacility", produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<APIResponseEntity<?>> getFacilityDetails(){
        try {
            List<String> response = service.getFacilityDetails();
            if(response != null){
                return getApiResponseEntity(response);
            }else{
                return ResponseEntity.status(StatusCode.OK)
                        .body(new APIResponseEntity<>(APIResponseEntity.Status.FAILED, Messages.FAILED,
                                "No Record Found"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(StatusCode.EXCEPTION)
                    .body(new APIResponseEntity<>(APIResponseEntity.Status.FAILED, Messages.FAILED,
                            e.getLocalizedMessage()));
        }
    }


    /**
     * method to get distinct filter details
     * @author yogaraj
     */
    @PostMapping(value = "/getFilterDetails",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<APIResponseEntity<?>> getFilterDetails(@RequestBody RequestModel requestModel){
        try {
            FilterModel responseModels = service.getFilterDetails(requestModel);
            return ResponseEntity.status(StatusCode.OK)
                    .body(new APIResponseEntity<>(APIResponseEntity.Status.SUCCESS, Messages.SUCCESS,
                            responseModels));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(StatusCode.EXCEPTION)
                    .body(new APIResponseEntity<>(APIResponseEntity.Status.FAILED, Messages.FAILED,
                            e.getLocalizedMessage()));
        }
    }

    /**
     * method to get patient  details
     * @author yogaraj
     */
    @GetMapping(value = "/getPatientDetails",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<APIResponseEntity<?>> getPatientDetails(@RequestBody RequestModel requestModel){
        try {
            List<HTRInfoEntity>  responseModels = service.getPatientDetails(requestModel);
            return ResponseEntity.status(StatusCode.OK)
                    .body(new APIResponseEntity<>(APIResponseEntity.Status.SUCCESS, Messages.SUCCESS,
                            responseModels));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(StatusCode.EXCEPTION)
                    .body(new APIResponseEntity<>(APIResponseEntity.Status.FAILED, Messages.FAILED,
                            e.getLocalizedMessage()));
        }
    }

}
