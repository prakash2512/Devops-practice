package com.healthcare.controller;


import com.healthcare.configuation.APIResponseEntity;
import com.healthcare.model.RequestModel;
import com.healthcare.service.BHIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@RestController
@RequestMapping("/bhi")
public class BHIController {


    @Autowired
    BHIService service;

    /**
     * method to upload BHI raw excel file
     * @author YOGARAJ
     * @param file
     * @return
     */
    @PostMapping("/excel/upload")
    public ResponseEntity<APIResponseEntity<?>> uploadBhiExcel(@RequestParam("file") MultipartFile file,
                                                               @RequestParam("month") LocalDate month,@RequestParam("state") String state,@RequestParam("program") String program){
        return service.uploadBhiExcel(file,month,state,program);
    }



    /**
     * method to upload BHI long and short term excel file
     * @author YOGARAJ
     * @param file
     * @return
     */
    @PostMapping("longShort/excel/upload")
    public ResponseEntity<APIResponseEntity<?>> longShortExcel(@RequestParam("file") MultipartFile file, @RequestParam("month") LocalDate month){
        return service.longShortExcelUpload(file,month);
    }

    /**
     * method to get Dashboard details for BHI file
     * @author YOGARAJ
     * @return
     */
    @PostMapping("get/dashboard/details")
    public ResponseEntity<APIResponseEntity<?>> uploadBhiExcel(@RequestBody RequestModel model){
        return service.getDashboardDetails(model);
    }


    /**
     * method to get patients details for BHI file
     * @author YOGARAJ
     * @return
     */
    @PostMapping("get/patients/details")
    public ResponseEntity<APIResponseEntity<?>> getPatients(@RequestBody RequestModel model){
        return service.getPatientsDetails(model);
    }

    /**
     * method to get patients Name for facility month
     * @author YOGARAJ
     * @return
     */
    @PostMapping("get/patientsNames")
    public ResponseEntity<APIResponseEntity<?>> getPatientsName(@RequestBody RequestModel model){
        return service.getPatientsName(model);
    }
}
