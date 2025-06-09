package com.healthcare.controller;
import com.healthcare.configuation.APIResponseEntity;
import com.healthcare.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/file")
public class FileUploadController {

    @Autowired
    FileUploadService service;

    /**
     * method to upload renew EMR login Excel file
     *
     * @author yogaraj
     */
    @PostMapping("/excel/upload")
    public ResponseEntity<APIResponseEntity<?>> stateAndFacilityUpload(@RequestParam("file") MultipartFile file) throws IOException {
        return service.stateAndFacilityUpload(file);
    }

    ;

    /**
     * method to get all the state and facilities
     * @author yogaraj
     */
    @GetMapping("/get/all/stateAndFacilities")
    public ResponseEntity<APIResponseEntity<?>> getAllStateAndFacilities() {
        return service.getAllStateAndFacilities();
    }



    /**
     * method to upload the file into database
     *
     * @param file
     * @param month
     * @return
     * @author sowmiyathangaraj
     */
    @PostMapping("/upload")
    public ResponseEntity<APIResponseEntity<?>> uploadExcelFile(@RequestParam("file") MultipartFile file, @RequestParam("month") LocalDate month, @RequestParam("programType") String programType) {
        return service.uploadExcelFile(file, month, programType);
    }

    /**
     * method to upload renew raw Excel file
     * @author yogaraj
     */
    @PostMapping("/raw/upload")
    public ResponseEntity<APIResponseEntity<?>> rawExcelUpload(@RequestParam("file") MultipartFile file,@RequestParam("month") LocalDate month, @RequestParam("programType") String programType) throws IOException {
        return service.rawExcelUpload(file, month, programType);
    };


    /**
     * method to upload the htr file into database
     * @author sowmiyathangaraj
     * @param file
     * @param month
     * @param programType
     * @return
     */
    @PostMapping("/htr/upload")
    public ResponseEntity<APIResponseEntity<?>> uploadHTRFile(@RequestParam("file") MultipartFile file, @RequestParam("month") LocalDate month, @RequestParam("programType") String programType){
        return service.uploadHTRFile(file, month, programType);
    }


    /**
     * method to upload htr long and short term files
     * @author sowmiyathangaraj
     * @param file
     * @return
     */
    @PostMapping("/htr/adk/upload")
    public ResponseEntity<APIResponseEntity<?>> uploadHTRLongAndShortFile(@RequestParam("file") MultipartFile file,LocalDate month){
        return service.uploadHTRLongAndShortFile(file,month);
    }


    /**
     * method to upload a facility file
     * @author sowmiyathangaraj
     */
    @PostMapping("/facility/upload")
    public ResponseEntity<APIResponseEntity<?>> uploadFacilityFile(@RequestParam("file") MultipartFile file){
        return service.uploadFacilityFile(file);
    }
}
