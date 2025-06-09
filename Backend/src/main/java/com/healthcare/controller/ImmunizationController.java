package com.healthcare.controller;


import com.healthcare.configuation.APIResponseEntity;
import com.healthcare.configuation.Messages;
import com.healthcare.entity.FileDataInfo;
import com.healthcare.model.ImmunizationResponse;
import com.healthcare.model.RequestModel;
import com.healthcare.service.ImmunizationService;
import com.healthcare.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ccm")
public class ImmunizationController {

    @Autowired
    ImmunizationService service;


    /**
     * method to get Immunization counts and patient Details  list
     * @author yogaraj
     */
    @PostMapping(value = "/get/immunization/details",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<APIResponseEntity<?>> getImmunizationDetails(@RequestBody RequestModel model){
        try {
            List<ImmunizationResponse> immunizationList = service.getImmunizationDetails(model);
            List<FileDataInfo>fileDataInfoList = service.getResult(model);

            if (!immunizationList.isEmpty()) {
                return ResponseEntity.status(StatusCode.OK).body(new APIResponseEntity<>(APIResponseEntity.Status.SUCCESS, Messages.SUCCESS, immunizationList));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(StatusCode.EXCEPTION)
                    .body(new APIResponseEntity<>(APIResponseEntity.Status.FAILED, Messages.FAILED,
                            e.getLocalizedMessage()));
        }
        return null;
    }
}
