package com.healthcare.service;


import com.healthcare.configuation.APIResponseEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;
public interface FileUploadService {

    ResponseEntity<APIResponseEntity<?>> stateAndFacilityUpload(MultipartFile file);

    ResponseEntity<APIResponseEntity<?>> getAllStateAndFacilities();

    ResponseEntity<APIResponseEntity<?>> uploadExcelFile(MultipartFile file, LocalDate month, String programType);

    ResponseEntity<APIResponseEntity<?>> rawExcelUpload(MultipartFile file, LocalDate month, String programType);

    ResponseEntity<APIResponseEntity<?>> uploadHTRFile(MultipartFile file, LocalDate month, String programType);
    /**
     * method to upload htr long and short term files
     * @author sowmiyathangaraj
     */
    ResponseEntity<APIResponseEntity<?>> uploadHTRLongAndShortFile(MultipartFile file,LocalDate month);


    /**
     * method to upload a facility file
     * @author sowmiyathangaraj
     */
    ResponseEntity<APIResponseEntity<?>> uploadFacilityFile(MultipartFile file);
}
