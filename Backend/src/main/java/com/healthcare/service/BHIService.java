package com.healthcare.service;


import com.healthcare.configuation.APIResponseEntity;
import com.healthcare.model.RequestModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Service
public interface BHIService {
    ResponseEntity<APIResponseEntity<?>> uploadBhiExcel(MultipartFile file, LocalDate month,String state,String program);

    ResponseEntity<APIResponseEntity<?>> getDashboardDetails(RequestModel model);

    ResponseEntity<APIResponseEntity<?>> getPatientsDetails(RequestModel model);

    ResponseEntity<APIResponseEntity<?>> longShortExcelUpload(MultipartFile file, LocalDate month);

    ResponseEntity<APIResponseEntity<?>> getPatientsName(RequestModel model);
}
