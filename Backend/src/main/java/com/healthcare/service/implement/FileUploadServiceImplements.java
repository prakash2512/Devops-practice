package com.healthcare.service.implement;

import com.healthcare.configuation.APIResponseEntity;
import com.healthcare.configuation.Messages;
import com.healthcare.entity.*;
import com.healthcare.entity.Enum;
import com.healthcare.service.FileUploadService;
import com.healthcare.utils.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FileUploadServiceImplements implements FileUploadService {

    @Autowired
    MongoTemplate mongoTemplate;


    /**
     * method to upload htr file into database
     *
     * @author sowmiyathangaraj
     */
    @Override
    public ResponseEntity<APIResponseEntity<?>> uploadHTRFile(MultipartFile file, LocalDate month, String programType) {
        if (file.isEmpty()) {
            return ResponseEntity.status(StatusCode.BAD_REQUEST)
                    .body(new APIResponseEntity<>(APIResponseEntity.Status.FAILED, Messages.FAILED,
                            "Please upload a valid HTR file!"));
        }
        try {
            insertHTRExcelData(file, month, programType);
            return ResponseEntity.status(StatusCode.OK)
                    .body(new APIResponseEntity<>(APIResponseEntity.Status.SUCCESS, Messages.SUCCESS,
                            "HTR File processed successfully!"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(StatusCode.EXCEPTION)
                    .body(new APIResponseEntity<>(APIResponseEntity.Status.FAILED, Messages.FAILED,
                            "Failed to process the file!"));
        }
    }

    public static int calculateAge(String birthdateStr) {
        // Clean up the input string by trimming and removing any non-breaking spaces
        birthdateStr = birthdateStr.trim().replaceAll("\u00A0", ""); // Remove non-breaking spaces

        // Define two possible date formats
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("MM/dd/yyyy"); // For format: "MM/dd/yyyy"
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd-MMM-yyyy"); // For format: "dd-MMM-yyyy"

        LocalDate birthdate = null;

        // Try to parse using the first format (MM/dd/yyyy)
        try {
            birthdate = LocalDate.parse(birthdateStr, formatter1);
        } catch (DateTimeParseException e1) {
            // If the first format fails, try the second format (dd-MMM-yyyy)
            try {
                birthdate = LocalDate.parse(birthdateStr, formatter2);
            } catch (DateTimeParseException e2) {
                // If both formats fail, throw an exception or handle the error
                throw new IllegalArgumentException("Invalid date format. Please use MM/dd/yyyy or dd-MMM-yyyy.");
            }
        }

        LocalDate currentDate = LocalDate.now();

        Period age = Period.between(birthdate, currentDate);

        return age.getYears();
    }


    /**
     * method to upload a facility file
     *
     * @param file
     * @return
     * @author sowmiyathangaraj
     */
    @Override
    public ResponseEntity<APIResponseEntity<?>> uploadFacilityFile(MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(StatusCode.BAD_REQUEST)
                    .body(new APIResponseEntity<>(APIResponseEntity.Status.FAILED, Messages.FAILED,
                            "Please upload a valid file!"));
        }
        try {
            LocalDate month = LocalDate.now();
            insertFacilityDetails(file, month);
            return ResponseEntity.status(StatusCode.OK)
                    .body(new APIResponseEntity<>(APIResponseEntity.Status.SUCCESS, Messages.SUCCESS,
                            "File processed successfully!"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(StatusCode.EXCEPTION)
                    .body(new APIResponseEntity<>(APIResponseEntity.Status.FAILED, Messages.FAILED,
                            "Failed to process the file! - " + e.getMessage()));
        }
    }


    /**
     * method to insert facility details in database
     *
     * @author sowmiyathangaraj
     */
    private void insertFacilityDetails(MultipartFile file, LocalDate month) {
        try (FileInputStream fis = new FileInputStream(convert(file))) {
            // Step 1: Parse the Excel file
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(1);

            // Step 2: Iterate through rows
            List<FacilityData> facilityList = new ArrayList<>();
            Map<String, Integer> headerMap = getHeaderMap(sheet);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                // Map each row to HTRData object
                FacilityData facilityData = new FacilityData();
                facilityData.setFacilityName(safeTrim(getCellStringValue(row.getCell(headerMap.get("Facility Name")))));
                facilityData.setState(safeTrim(getCellStringValue(row.getCell(headerMap.get("State")))));
                facilityData.setActiveStatus("1");
                facilityList.add(facilityData);
            }

            // Step 3: Save data into MongoDB
            if (!facilityList.isEmpty()) {
                mongoTemplate.insertAll(facilityList);
            }
            // Step 4: Save file metadata
            FileInfo metadata = new FileInfo();
            metadata.setFileName(file.getName());
            metadata.setMonth(LocalDate.now());
            metadata.setUploadTime(new Date().toInstant());
            mongoTemplate.save(metadata);
            System.out.println("Data and metadata saved successfully!");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * method to upload a htr long and short term files
     *
     * @author sowmiyathangaraj
     */
    @Override
    public ResponseEntity<APIResponseEntity<?>> uploadHTRLongAndShortFile(MultipartFile file, LocalDate reportedDate) {
        if (file.isEmpty()) {
            return ResponseEntity.status(StatusCode.BAD_REQUEST)
                    .body(new APIResponseEntity<>(APIResponseEntity.Status.FAILED, Messages.FAILED,
                            "Please upload a valid file!"));
        }
        try {

            System.out.println("File downloaded successfully from S3.");
            LocalDate month = LocalDate.now();
            insertHTRShortAndLongData(file, month, reportedDate);
            return ResponseEntity.status(StatusCode.OK)
                    .body(new APIResponseEntity<>(APIResponseEntity.Status.SUCCESS, Messages.SUCCESS,
                            "File processed successfully!"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(StatusCode.EXCEPTION)
                    .body(new APIResponseEntity<>(APIResponseEntity.Status.FAILED, Messages.FAILED,
                            "Failed to process the file! - " + e.getMessage()));
        }

    }


    /**
     * method to insert htr short and long term data
     *
     * @author sowmiyathangaraj
     */
    private void insertHTRShortAndLongData(MultipartFile file, LocalDate month, LocalDate reportedDate) {
        try (FileInputStream fis = new FileInputStream(convert(file))) {
            // Step 1: Parse the Excel file
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);

            // Step 2: Iterate through rows
            List<HtrShortAndLongData> htrDataList = new ArrayList<>();
            Map<String, Integer> headerMap = getHeaderMap(sheet);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                // Map each row to HTRData object
                HtrShortAndLongData htrData = new HtrShortAndLongData();
                //   String stringDate = getCellStringValue(row.getCell(headerMap.get("Month"))).trim();
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
                // htrData.setMonth(formatter.parse(stringDate));
                htrData.setMonth(String.valueOf(reportedDate.getMonth()));
                htrData.setYear(reportedDate.getYear());
                htrData.setFacility(safeTrim(getCellStringValue(row.getCell(headerMap.get("Facility Name")))));
                htrData.setState(safeTrim(getCellStringValue(row.getCell(headerMap.get("STATE")))));
                htrData.setAverageCensus(safeTrim(getCellStringValue(row.getCell(headerMap.get("Average Census")))));
                htrData.setShortNationAverage(safeTrim(getCellStringValue(row.getCell(headerMap.get("Short-stay -National average")))));
                htrData.setShortStateAverage(safeTrim(getCellStringValue(row.getCell(headerMap.get("Short-stay- State average")))));
                htrData.setShortAverage(safeTrim(getCellStringValue(row.getCell(headerMap.get("Short-stay-Average")))));
                htrData.setLongNationAverage(safeTrim(getCellStringValue(row.getCell(headerMap.get("Long-Stay-National average")))));
                htrData.setLongStateAverage(safeTrim(getCellStringValue(row.getCell(headerMap.get("Long-Stay-State average")))));
                htrData.setLongAverage(safeTrim(getCellStringValue(row.getCell(headerMap.get("Long-Stay-Average")))));

                if (htrData.getAverageCensus() != null) {
                    String averageCensusStr = htrData.getAverageCensus().trim();

                    if (averageCensusStr.matches("\\d+(\\.\\d+)?")) { // Check if numeric
                        if (getCellStringValue(row.getCell(headerMap.get("Total no. of .Admits"))) != null) {
                            String totalAdmitsStr = safeTrim(getCellStringValue(row.getCell(headerMap.get("Total no. of .Admits"))));
                            htrData.setTotalNoOfAdmits(totalAdmitsStr);
                            try {
                                // Parse values to numbers
                                int totalCount = (int) Double.parseDouble(totalAdmitsStr);
                                double averageCensus = Double.parseDouble(averageCensusStr); // Keep as double for division

                                // Avoid division by zero
                                if (averageCensus != 0) {
                                    int adk = (int) ((totalCount / averageCensus) * 12000); // Perform double division
                                    htrData.setAdk(adk);
                                } else {
                                    htrData.setAdk(0);
                                }
                            } catch (NumberFormatException e) {
                                System.err.println("Invalid numeric value for Total Admits or Average Census.");
                                htrData.setAdk(0); // Set ADK to 0 for invalid numeric values
                            }
                        } else {
                            htrData.setAdk(0); // Set ADK to 0 if Total Admits is null
                        }
                    } else {
                        htrData.setAdk(0); // Set ADK to 0 for non-numeric Average Census
                    }
                } else {
                    htrData.setAdk(0); // Set ADK to 0 if Average Census is null
                }
                htrDataList.add(htrData);
            }

            // Step 3: Save data into MongoDB
            if (!htrDataList.isEmpty()) {
                mongoTemplate.insertAll(htrDataList);
            }

            // Step 4: Save file metadata
            FileInfo metadata = new FileInfo();
            metadata.setFileName(file.getName());
            metadata.setMonth(month);
            metadata.setUploadTime(new Date().toInstant());
            mongoTemplate.save(metadata);
            System.out.println("Data and metadata saved successfully!");

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error processing the Excel file!", e);
        }
    }

    /**
     * method to get header map from the Excel
     *
     * @author sowmiyathangaraj
     */
    private Map<String, Integer> getHeaderMap(Sheet sheet) {
        Map<String, Integer> headerMap = new HashMap<>();
        Row headerRow = sheet.getRow(1);
        if (headerRow != null) {
            for (Cell cell : headerRow) {
                String headerName = cell.getStringCellValue().trim();
                headerMap.put(headerName, cell.getColumnIndex());
            }
        }
        return headerMap;
    }


    /**
     * method to insert HTR excel data into database
     *
     * @author sowmiyathangaraj
     */
    private void insertHTRExcelData(MultipartFile file, LocalDate month, String programType) {
        //try{
        List<String> errorList = new ArrayList<>();
        try {
            FileInfo metadata = new FileInfo();
            metadata.setFileName(file.getName());
            metadata.setMonth(month);
            metadata.setUploadTime(new Date().toInstant());
            FileInfo savedMetadata = mongoTemplate.save(metadata);

            // Step 1: Get required program info from the database
            List<ProgramsInfoEntity> programsInfoList = mongoTemplate.find(
                    Query.query(
                            Criteria.where(ProgramsInfoEntity.Fields.programName).is(programType)
                                    .and(ProgramsInfoEntity.Fields.isRequired).is(true)
                    ),
                    ProgramsInfoEntity.class
            );


            // Create a map of required columns and their data types
            Map<String, String> requiredColumnsMap = programsInfoList.stream()
                    .collect(Collectors.toMap(ProgramsInfoEntity::getProgramColumnName, ProgramsInfoEntity::getColumnDataType));


            // Step 2: Read the Excel file
            FileInputStream fis = new FileInputStream(convert(file));
            Workbook workbook = WorkbookFactory.create(fis);
            Sheet sheet = workbook.getSheetAt(0);

            // Step 3: Extract headers (first row)
            Row headerRow = sheet.getRow(0);
            List<String> headers = new ArrayList<>();
            for (Cell headerCell : headerRow) {
                headers.add(headerCell.getStringCellValue().trim());
            }
            Date convertedMonth = Date.from(month.atStartOfDay(ZoneId.of("UTC")).toInstant());
            List<HTRInfoEntity> excelDataList = new ArrayList<>();

            // Step 4: Read data for each row and map to headers
            for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row row = sheet.getRow(rowNum);
                HTRInfoEntity excelData = new HTRInfoEntity();
                excelData.setExcelId(savedMetadata.getId());
                excelData.setReportDate(convertedMonth);
                excelData.setProgramType(Enum.HTR);
                String mon = month.getMonth().toString().substring(0, 3);
                System.out.println(mon);
                excelData.setMonth(mon);
                excelData.setYear(month.getYear());

                for (int colNum = 0; colNum < headers.size(); colNum++) {
                    String header = headers.get(colNum);
                    if (requiredColumnsMap.containsKey(header)) {
                        Cell cell = row.getCell(colNum, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        String columnDataType = requiredColumnsMap.get(header);
                        System.out.println("headers and datatype : " + header + "," + columnDataType);
                        // try {
                        Object cellValue = getCellValueWithType(cell, columnDataType);
                        if (cellValue != null) {
                            setHTRDataField(excelData, header, cellValue);
                            String patientName = excelData.getFirstName() + " " + excelData.getLastName();
                            excelData.setPatientName(patientName);
                        }
//                            } catch (IllegalArgumentException e) {
//                                errorList.add("Row " + (rowNum + 1) + ", Column '" + header + "': " + e.getMessage());
//                            }
                    }
                }
                excelDataList.add(excelData);
            }
            if (!errorList.isEmpty()) {
                throw new RuntimeException("Error found during excel processing " + errorList);
            }
            mongoTemplate.insertAll(excelDataList); // Insert all records at once
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // } catch (Exception e) {
        //    throw new RuntimeException(e);
        // }

    }


    /**
     * method to set HTR Data Field and store into database
     *
     * @author sowmiyathangaraj
     */
    private void setHTRDataField(HTRInfoEntity excelData, String header, Object value) throws ParseException {
        if (value.equals("")) {
            value = "0";
        }
        System.out.println("fieldName : " + header + "value : " + value);

        // Define the date format to parse the strings
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");

        switch (header) {

            case "Facility":
                excelData.setFacility(((String) value).toUpperCase());
                break;

            case "Cardio- Pulmonary":
                excelData.setCardioPulmonary(((String) value));
                break;
            case "Cardio Program":
                excelData.setCardioProgram(((String) value));
                break;

            case "First Name":
                excelData.setFirstName((String) value);
                break;

            case "Last Name":
                excelData.setLastName((String) value);
                break;

            case "State":
                excelData.setState((String) value);
                break;

            case "Gender":
                excelData.setGender((String) value);
                break;

            case "Payer Source":
                excelData.setTransferByPayerGroup((String) value);
                break;

            case "Stay Status":
                excelData.setStayStatus((String) value);
                break;

            case "AdvanceDirective":
                excelData.setAdvanceDirective((String) value);
                break;

            case "Length of Stay":
                excelData.setLengthOfStay(Integer.parseInt(value.toString()));
                break;

            case "Planned/ Unplanned":
                excelData.setTransferByCategory((String) value);
                break;

            case "Practitioner Ordering Transfer":
                excelData.setProvider((String) value);
                break;

            case "Hospital Name":
                excelData.setHospitalName((String) value);
                break;

            case "S/sx observed, factor leading to hospitalization":
                excelData.setReport((String) value);
                break;

            case "Disposition":
                excelData.setTransferByDisposition((String) value);
                break;

            case "RVL Study within 72 hours":
                excelData.setRVLStudy(Boolean.parseBoolean(value.toString()));
                break;

            case "Notes/Intervention":
                excelData.setNotes((String) value);
                break;

            case "Diagnosis":
                excelData.setDiagnosis((String) value);
                break;

            case "Categories":
                excelData.setCategories((String) value);
                break;

            case "Status":
                excelData.setStatus((String) value);
                break;

            case "Date of Birth (mo/dd/yyyy)":
                System.out.println((String) value + "hii");
                excelData.setDateOfBirth((String) value);
                excelData.setAge(calculateAge((String) value));
                break;

            case "Hospitalization for the past 12 months":
                if ("--".equals(value)) {
                    // Handle the case where the value is "--"
                    excelData.setHospitalizationDate(null);
                } else if (value instanceof String && isValidDate(value.toString(), dateFormat)) {
                    try {
                        excelData.setHospitalizationDate(dateFormat.parse(value.toString()));
                    } catch (ParseException e) {
                        throw new IllegalArgumentException("Invalid date format for " + header + ": " + value, e);
                    }
                } else {
                    excelData.setHospitalizationDate(null); // Handle other invalid cases
                }
                break;

            case "Date of recent admission (mo/dd/yyyy)":
                excelData.setDateOfRecentAdmission((String) value);
                break;

            case "Transfer date (dd/mo/yyyy)", "Transfer date (mo/dd/yyyy)":
                if (value instanceof Date) {
                    excelData.setTransferDate((Date) value);
                } else if (value instanceof String) {
                    try {
                        SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MMM-yyyy");
                        inputFormat.setLenient(false);
                        excelData.setTransferDate(inputFormat.parse((String) value));
                    } catch (ParseException e) {
                        throw new IllegalArgumentException("Invalid date format for Date of Birth: " + value, e);
                    }
                } else {
                    throw new IllegalArgumentException("Invalid data type for Date of Birth: " + value.getClass().getName());
                }
                break;

            case "Transfer time (Military Time)":
                System.out.println(value);
                try {
                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
//                    excelData.setTransferTime(LocalTime.parse(value.toString(), timeFormatter));
                    excelData.setTransferTime(LocalTime.parse(value.toString().trim(), timeFormatter));
                } catch (DateTimeParseException e) {
                    throw new IllegalArgumentException("Invalid Transfer time format for " + header + ": " + value, e);
                }
                break;


            case "Unit":
                excelData.setUnit((String) value);
                break;

            case "Room":
                excelData.setRoom((String) value);
                break;

            case "Clinical Risk Indicator":
                String valueStr = value.toString();

                if (valueStr.contains(".")) {
                    valueStr = valueStr.split("\\.")[0];
                }

                try {
                    excelData.setClinicalRiskIndicator(Integer.parseInt(valueStr));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid value for Clinical Risk Indicator: " + value);
                }
                break;

            default:
                break;
        }
    }


    // Method to convert a string date to HH:mm format
    public static String convertToTimeFormat(String dateString) {
        try {
            // Define the input format of the string
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MMM-yyyy");

            // Parse the input string into a Date object
            Date date = inputFormat.parse(dateString);

            // Define the output format to convert it to HH:mm
            SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm");

            // Format the Date object into the desired time format
            return outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Return null or handle exception as needed
        }
    }


    private boolean isValidDate(String dateStr, SimpleDateFormat dateFormat) {
        if (dateStr == null || dateStr.trim().isEmpty() || "--".equals(dateStr)) {
            return false; // Treat "--" and empty strings as invalid
        }
        dateFormat.setLenient(false); // Ensure strict parsing
        try {
            dateFormat.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }


    @Override
    public ResponseEntity<APIResponseEntity<?>> stateAndFacilityUpload(MultipartFile file) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            List<StateAndFacilities> StateAndFacilitiesEntites = new ArrayList<>();
            Sheet sheet = workbook.getSheetAt(1);
            Map<String, Integer> headers = new HashMap<>();
            Row headerRow = sheet.getRow(0);
            for (Cell cell : headerRow) {
                if (cell.getCellType() == CellType.BLANK) continue;
                String cellValue = cell.getStringCellValue().trim();
                String stringWithoutSpaces = cellValue.replace(" ", "");
                int cellIdx = cell.getColumnIndex();
                headers.put(stringWithoutSpaces, cellIdx);
            }
            ;

            int rowCount = sheet.getLastRowNum();
            for (int j = 1; j < rowCount; j++) {
                Row row = sheet.getRow(j);
                StateAndFacilities stateAndFacilities = new StateAndFacilities(
                        safeTrim(getCellStringValue(row.getCell(headers.get("FacilityName")))),
                        safeTrim(getCellStringValue(row.getCell(headers.get("State")))),
                        safeTrim(getCellStringValue(row.getCell(headers.get("Coordinator"))))
                );
                StateAndFacilitiesEntites.add(stateAndFacilities);
            }

            if (!StateAndFacilitiesEntites.isEmpty()) {
                mongoTemplate.insertAll(StateAndFacilitiesEntites);
            }
            ;
            return ResponseEntity.status(StatusCode.OK).body(new APIResponseEntity<>(APIResponseEntity.Status.SUCCESS, "hii", "File uploaded Successfully"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<APIResponseEntity<?>> getAllStateAndFacilities() {

        List<StateAndFacilities> stateAndFacilitiesList = mongoTemplate.findAll(StateAndFacilities.class);

        if (stateAndFacilitiesList.isEmpty()) {
            return ResponseEntity.status(StatusCode.OK).body(new APIResponseEntity<>(APIResponseEntity.Status.FAILED, "NO DATA FOUND", null));
        }
        return ResponseEntity.status(StatusCode.OK).body(new APIResponseEntity<>(APIResponseEntity.Status.SUCCESS, Messages.SUCCESS, stateAndFacilitiesList));
    }

    @Override
    public ResponseEntity<APIResponseEntity<?>> uploadExcelFile(MultipartFile file, LocalDate month, String programType) {
        if (file.isEmpty()) {
            return ResponseEntity.status(StatusCode.BAD_REQUEST)
                    .body(new APIResponseEntity<>(APIResponseEntity.Status.FAILED, Messages.FAILED,
                            "Please upload a valid file!"));
        }
        try {
            insertExcelData(file, month, programType);
            return ResponseEntity.status(StatusCode.OK)
                    .body(new APIResponseEntity<>(APIResponseEntity.Status.SUCCESS, Messages.SUCCESS,
                            "File processed successfully!"));
        } catch (Exception e) {
            log.error("e: ", e);
            return ResponseEntity.status(StatusCode.EXCEPTION)
                    .body(new APIResponseEntity<>(APIResponseEntity.Status.FAILED, Messages.FAILED,
                            "Failed to process the file!"));
        }
    }

    @Override
    public ResponseEntity<APIResponseEntity<?>> rawExcelUpload(MultipartFile file, LocalDate month, String programType) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            List<MedEliteReports> medEliteReportsList = new ArrayList<>();
            int sheetIndex = workbook.getActiveSheetIndex();
            Sheet sheet = workbook.getSheetAt(sheetIndex);
            Map<String, Integer> headers = new HashMap<>();
            Row headerRow = sheet.getRow(0);

            for (Cell cell : headerRow) {
                if (cell.getCellType() == CellType.BLANK) continue;
                String cellValue = cell.getStringCellValue().trim();
                String stringWithoutSpaces = cellValue.replace(" ", "");
                int cellIdx = cell.getColumnIndex();
                headers.put(stringWithoutSpaces, cellIdx);
            }
            ;

            int rowCount = sheet.getLastRowNum();
            for (int j = 1; j < rowCount; j++) {
                Row row = sheet.getRow(j);
                String dosDate = safeTrim(getCellStringValue(row.getCell(headers.get("DoS"))));
                String dosMonth = setMonth(dosDate);
                int dosYear = setYear(dosDate);
                String patientName = safeTrim(getCellStringValue(row.getCell(headers.get("Patientname"))));
                String dos = safeTrim(getCellStringValue(row.getCell(headers.get("DoS"))));
                String dob = safeTrim(getCellStringValue(row.getCell(headers.get("Dateofbirth"))));
                int patientAge = Integer.parseInt(safeTrim(getCellStringValue(row.getCell(headers.get("Patientage")))));
                String patientGender = safeTrim(getCellStringValue(row.getCell(headers.get("Patientgender"))));
                String facilityName = safeTrim(getCellStringValue(row.getCell(headers.get("Referringfacility"))));
                String facilityNameConverted = removeTextAfterBrackets(facilityName);
                String template = safeTrim(getCellStringValue(row.getCell(headers.get("Template"))));
                String loginBy = safeTrim(getCellStringValue(row.getCell(headers.get("Loginby"))));
                String signedAt = safeTrim(getCellStringValue(row.getCell(headers.get("Signedat"))));
                String signedBy = safeTrim(getCellStringValue(row.getCell(headers.get("Signedby"))));
                String serviceDiagnosis = safeTrim(getCellStringValue(row.getCell(headers.get("Servicediagnosis"))));
                String cptCode = safeTrim(getCellStringValue(row.getCell(headers.get("CPT(Studycode)"))));
                String billingDate = safeTrim(getCellStringValue(row.getCell(headers.get("BILLING_DATE"))));
                String previousVisitDate = safeTrim(getCellStringValue(row.getCell(headers.get("PREV_VISIT_DATE"))));
                String ccphmVisit = safeTrim(getCellStringValue(row.getCell(headers.get("CCPHM_VISIT"))));
                String diagnosis = safeTrim(getCellStringValue(row.getCell(headers.get("DIAGNOSIS"))));
                String diagnosList = safeTrim(getCellStringValue(row.getCell(headers.get("DIAGNOS_LIST"))));
                String carePlan = safeTrim(getCellStringValue(row.getCell(headers.get("CAREPLAN"))));
                String caregaps = safeTrim(getCellStringValue(row.getCell(headers.get("CAREGAPS"))));
                String patientConditionDiag1 = safeTrim(getCellStringValue(row.getCell(headers.get("PATIENT_CONDITION_DIAG_1"))));
                String diagnosList2 = safeTrim(getCellStringValue(row.getCell(headers.get("DIAGNOS_LIST2"))));
                String carePlan2 = safeTrim(getCellStringValue(row.getCell(headers.get("CAREPLAN2"))));
                String careGaps2 = safeTrim(getCellStringValue(row.getCell(headers.get("CAREGAPS2"))));
                String patientConditionDiag2 = safeTrim(getCellStringValue(row.getCell(headers.get("PATIENT_CONDITION_DIAG_2"))));

                String pneumococcal = safeTrim(getCellStringValue(row.getCell(headers.get("IMM_PNEUMO_Y"))));
                String influenza = safeTrim(getCellStringValue(row.getCell(headers.get("IMM_FLU_Y"))));
                String prevnar = safeTrim(getCellStringValue(row.getCell(headers.get("IMM_PREVNAR_Y"))));
                String covidVaccine1 = safeTrim(getCellStringValue(row.getCell(headers.get("IMM_COVID_Y"))));
                String covidVaccine2 = safeTrim(getCellStringValue(row.getCell(headers.get("IMM_COVID_Y2"))));
                String covidBooster = safeTrim(getCellStringValue(row.getCell(headers.get("IMM_COVID_Y3"))));


                String fallRiskAssessment = safeTrim(getCellStringValue(row.getCell(headers.get("FALL_RISK_1"))));
                String fallRiskCarePlan = safeTrim(getCellStringValue(row.getCell(headers.get("FALL_RISK_2"))));
                String skinRiskAssessment = safeTrim(getCellStringValue(row.getCell(headers.get("SKIN_RISK_1"))));
                String skinRiskCategory = safeTrim(getCellStringValue(row.getCell(headers.get("SKIN_RISK_2"))));
                String skinRiskStore = safeTrim(getCellStringValue(row.getCell(headers.get("SKIN_RISK_3"))));
                String skinRiskCarePlan = safeTrim(getCellStringValue(row.getCell(headers.get("SKIN_RISK_5"))));
                String catheter = safeTrim(getCellStringValue(row.getCell(headers.get("CAT1"))));
                String catheterType = safeTrim(getCellStringValue(row.getCell(headers.get("CAT2"))));
                String catheterCarePlan = safeTrim(getCellStringValue(row.getCell(headers.get("CAT4"))));
                String catheterIndication = safeTrim(getCellStringValue(row.getCell(headers.get("CAT5"))));
                String advanceDirective = safeTrim(getCellStringValue(row.getCell(headers.get("ADV_DIR1"))));
                String advanceDirectiveTypes = safeTrim(getCellStringValue(row.getCell(headers.get("ADV_DIR3"))));
                String advCarePlan = safeTrim(getCellStringValue(row.getCell(headers.get("ADV_DIR5"))));
                String advDoctorsOrder = safeTrim(getCellStringValue(row.getCell(headers.get("ADV_DIR4"))));
                MedEliteReports medEliteReports = new MedEliteReports();
                medEliteReports.setAdvDoctorsOrder(advDoctorsOrder);
                medEliteReports.setAdvCarePlan(advCarePlan);
                medEliteReports.setAdvanceDirectivesTypes(advanceDirectiveTypes);
                medEliteReports.setAdvanceDirective(advanceDirective);
                medEliteReports.setCarePlan(catheterCarePlan);
                medEliteReports.setCatheterIndication(catheterIndication);
                medEliteReports.setCatheterType(catheterType);
                medEliteReports.setCatheter(catheter);
                //medEliteReports.
                medEliteReports.setPneumococcal(pneumococcal);
                medEliteReports.setInfluenza(influenza);
                medEliteReports.setPrevnar(prevnar);
                medEliteReports.setCovidVaccine1(covidVaccine1);
                medEliteReports.setCovidVaccine2(covidVaccine2);
                medEliteReports.setCovidBooster(covidBooster);
                medEliteReports.setPatientName(patientName);
                medEliteReports.setDos(dos);
                medEliteReports.setDob(dob);
                medEliteReports.setPatientAge(patientAge);
                medEliteReports.setPatientGender(patientGender);
                medEliteReports.setFacilityName(facilityNameConverted);
                medEliteReports.setTemplate(template);
                medEliteReports.setLoginBy(loginBy);
                medEliteReports.setSignedAt(signedAt);
                medEliteReports.setSignedBy(signedBy);
                medEliteReports.setServiceDiagnosis(serviceDiagnosis);
                medEliteReports.setCptCode(cptCode);
                medEliteReports.setBillingDate(billingDate);
                medEliteReports.setPreviousVisitDate(previousVisitDate);
                medEliteReports.setCcphmVisit(ccphmVisit);
                medEliteReports.setDiagnosis(diagnosis);
                medEliteReports.setDiagnosList(diagnosList);
                medEliteReports.setCarePlan(carePlan);
                medEliteReports.setCareGaps(caregaps);
                medEliteReports.setPatientConditionDiag1(patientConditionDiag1);
                medEliteReports.setDiagnosList_2(diagnosList2);
                medEliteReports.setCarePlan2(carePlan2);
                medEliteReports.setCaregaps2(careGaps2);
                medEliteReports.setPatientConditionDiag2(patientConditionDiag2);
                medEliteReports.setMonth(dosMonth);
                medEliteReports.setYear(dosYear);
                medEliteReports.setProgramType(programType);

                medEliteReportsList.add(medEliteReports);
            }
            if (!medEliteReportsList.isEmpty()) {
                mongoTemplate.insertAll(medEliteReportsList);
            }
            ;
            return ResponseEntity.status(StatusCode.OK).body(new APIResponseEntity<>(APIResponseEntity.Status.SUCCESS, "hii", "File uploaded Successfully"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * method to remove the brackets words in facility name  excel
     *
     * @author yogaraj
     */

    public static String removeTextAfterBrackets(String input) {
        if (input == null) {
            return "";  // Handle null input
        }
        // Use regular expression to remove everything inside and including the brackets
        return input.replaceAll("\\(.*\\)", "").trim().toUpperCase();
    }


    /**
     * method to insert excel data
     *
     * @param month
     * @param programType
     * @author sowmiyathangaraj
     */
    private void insertExcelData(MultipartFile file, LocalDate month, String programType) throws IOException {
        List<String> errorList = new ArrayList<>();
        // try {
        FileInfo metadata = new FileInfo();
        metadata.setFileName(file.getName());
        metadata.setMonth(month);
        metadata.setUploadTime(new Date().toInstant());
        FileInfo savedMetadata = mongoTemplate.save(metadata);

        // Step 1: Get required program info from the database
        List<ProgramsInfoEntity> programsInfoList = mongoTemplate.find(
                Query.query(
                        Criteria.where(ProgramsInfoEntity.Fields.programName).is(programType)
                                .and(ProgramsInfoEntity.Fields.isRequired).is(true)
                ),
                ProgramsInfoEntity.class
        );

        // Create a map of required columns and their data types
        Map<String, String> requiredColumnsMap = programsInfoList.stream()
                .collect(Collectors.toMap(ProgramsInfoEntity::getProgramColumnName, ProgramsInfoEntity::getColumnDataType));

        // Step 2: Read the Excel file
        FileInputStream fis = new FileInputStream(convert(file));
        Workbook workbook = WorkbookFactory.create(fis);
        int sheetIdx = workbook.getActiveSheetIndex();
        Sheet sheet = workbook.getSheetAt(sheetIdx);

        // Step 3: Extract headers (first row)
        Row headerRow = sheet.getRow(0);
        List<String> headers = new ArrayList<>();

        for (Cell headerCell : headerRow) {
            headers.add(headerCell.getStringCellValue().trim());
        }

        Date convertedMonth = Date.from(month.atStartOfDay(ZoneId.of("UTC")).toInstant());

        List<FileDataInfo> excelDataList = new ArrayList<>();

        // Step 4: Read data for each row and map to headers
        for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
            Row row = sheet.getRow(rowNum);
            FileDataInfo excelData = new FileDataInfo();
            excelData.setExcelId(savedMetadata.getId());
            excelData.setReportDate(convertedMonth);
            excelData.setProgramType(Enum.CCM);

            for (int colNum = 0; colNum < headers.size(); colNum++) {
                String header = headers.get(colNum);
                if (requiredColumnsMap.containsKey(header)) {
                    Cell cell = row.getCell(colNum, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    String columnDataType = requiredColumnsMap.get(header);
                    try {
                        Object cellValue = getCellValueWithType(cell, columnDataType);
                        setExcelDataField(excelData, header, cellValue);
                    } catch (IllegalArgumentException e) {
                        errorList.add("Row " + (rowNum + 1) + ", Column '" + header + "': " + e.getMessage());
                    }
                }
            }
            excelDataList.add(excelData);
        }
        if (!errorList.isEmpty()) {
            throw new RuntimeException("Error found during excel processing " + errorList);
        }
        mongoTemplate.insertAll(excelDataList); // Insert all records at once
        // } catch (Exception e) {
        //    throw new RuntimeException(e);
        //}
    }


    /**
     * method to set excel data into collection
     *
     * @param excelData
     * @param header
     * @param value
     * @author sowmiyathangaraj
     */
    private void setExcelDataField(FileDataInfo excelData, String header, Object value) {

        if (value.equals("")) {
            value = "0";
        }

        System.out.println(header + "------> " + value);

        switch (header) {
            case "Patient name":
                excelData.setPatientName((String) value);
                break;
            case "Patient age":
                if (value instanceof String ageString) {
                    try {
                        excelData.setPatientAge(Integer.parseInt(ageString.replaceAll("\"", "").trim())); // Remove quotes and parse
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid number format for Patient age: " + ageString);
                    }
                } else if (value instanceof Number) {
                    excelData.setPatientAge(((Number) value).intValue());
                }
                break;

            case "IMM_PNEUMO_Y":
                excelData.setPneumococcal((String) value);
                break;
            case "HOSPITALIZED_CCM_DIAGNOSES":
                String text = (String) value;
                Pattern pattern = Pattern.compile("\\d{1,2}/\\d{1,2}/\\d{2,4}");
                Matcher matcher = pattern.matcher(text);

                String firstDate = null;
                String secondDate = null;

                int count = 0;

                while (matcher.find()) {
                    if (count == 0) {
                        firstDate = matcher.group();
                    } else if (count == 1) {
                        secondDate = matcher.group();
                        break;
                    }
                    count++;
                }

                if (firstDate != null && secondDate != null) {
                    excelData.setHospitalizedFromDate(convertToDate(firstDate));
                    excelData.setHospitalizedEndDate(convertToDate(secondDate));
                    excelData.setDaysOfStay((int) (ChronoUnit.DAYS.between(convertToDate(firstDate), convertToDate(secondDate)) + 1));

                }


                excelData.setHospitalizedDate((String) value);
                break;
            case "RESIDENT_HOSPITALIZED":
                excelData.setResidentHospitalized((String) value);
                break;

            case "HOSPITALIZED_DIAGNOSES":
                excelData.setHospitalizedDiagnosis((String) value);
                break;
            case "Date of birth":
                excelData.setDob((String) value);
                break;

            case "ER_VISIT":
                excelData.setErVisit((String) value);
                break;
            case "Patient gender":
                excelData.setPatientGender((String) value);
                break;
            case "Signed by":
                excelData.setPhysician((String) value);
                break;

            case "IMM_FLU_Y":
                excelData.setInfluenza((String) value); // Example setter
                break;
            case "IMM_PREVNAR_Y":
                excelData.setPrevnar((String) value); // Add appropriate setters
                break;
            case "DoS":
                excelData.setDos((String) value); // Add appropriate setters
                excelData.setMonth(setMonth((String) value));
                excelData.setYear(setYear((String) value));
                break;
            case "IMM_COVID_Y": // Replace with actual field names
                excelData.setCovidVaccine1((String) value); // Add appropriate setters
                break;

            case "IMM_COVID_Y2":
                excelData.setCovidVaccine2((String) value);
                break;
            case "IMM_COVID_Y3":
                excelData.setCovidBooster((String) value);
                break;

            case "FALL_RISK_1":

                //   excelData.setFallRiskAssessment((boolean) value);
                if ((boolean) value) {
                    excelData.setFallRiskAssessment("Yes");
                } else {
                    excelData.setFallRiskAssessment("No");
                }

                break;
            case "FALL_RISK_2":
                //   excelData.setFallRiskCarePlan((boolean) value); // Example setter

                if ((boolean) value) {
                    excelData.setFallRiskCarePlan("Yes");
                } else {
                    excelData.setFallRiskCarePlan("No");
                }

                break;
            case "SKIN_RISK_1":
                // excelData.setSkinRiskAssessment((boolean) value); // Add appropriate setters
                if ((boolean) value) {
                    excelData.setSkinRiskAssessment("Yes");
                } else {
                    excelData.setSkinRiskAssessment("No");
                }
                break;

            case "SKIN_RISK_2":
                excelData.setSkinRiskCategory((String) value); // Add appropriate setters
                break;

            case "SKIN_RISK_3":

                excelData.setSkinRiskStore((String) value);
                break;
            case "SKIN_RISK_5":
                //excelData.setSkinRiskCarePlan((boolean) value);
                if ((boolean) value) {
                    excelData.setSkinRiskCarePlan("Yes");
                } else {
                    excelData.setSkinRiskCarePlan("No");
                }
                break;

            case "CAT1":
                // excelData.setCatheter((boolean) value);
                if ((boolean) value) {
                    excelData.setCatheter("Yes");
                } else {
                    excelData.setCatheter("No");
                }

                break;

            case "CAT2":
                excelData.setCatheterType((String) value);
                break;
            case "CAT4":
                //  excelData.setCatheterCarePlan((boolean) value);
                if ((boolean) value) {
                    excelData.setCatheterCarePlan("Yes");
                } else {
                    excelData.setCatheterCarePlan("No");
                }
                break;

            case "CAT5":
                //  excelData.setCatheterIndication((boolean) value);

                if ((boolean) value) {
                    excelData.setCatheterIndication("Yes");
                } else {
                    excelData.setCatheterIndication("No");
                }
                break;
            case "ADV_DIR1":
                // excelData.setAdvanceDirective((boolean) value);
                if ((boolean) value) {
                    excelData.setAdvanceDirective("Yes");
                } else {
                    excelData.setAdvanceDirective("No");
                }
                break;
            case "ADV_DIR3":
                excelData.setAdvanceDirectivesTypes((String) value);
                break;

            case "ADV_DIR5":
                //    excelData.setAdvCarePlan((boolean) value);
                if ((boolean) value) {
                    excelData.setAdvCarePlan("Yes");
                } else {
                    excelData.setAdvCarePlan("No");
                }
                break;
            case "ADV_DIR4":
                //   excelData.setAdvDoctorsOrder((boolean) value);
                if ((boolean) value) {
                    excelData.setAdvDoctorsOrder("Yes");
                } else {
                    excelData.setAdvDoctorsOrder("No");
                }
                break;
            case "DIAGNOS_LIST":
                excelData.setDiagnosList((String) value);
                break;
            case "CAREGAPS":
                excelData.setCareGaps((String) value);
                excelData.setShowCareGaps1(true);
                break;
            case "CAREGAPS2":
                excelData.setCareGaps2((String) value);
                excelData.setShowCareGaps2(true);
                break;
            case "DIAGNOS_LIST2":
                excelData.setDiagnosList2((String) value);
                break;
            case "IS_CARE_GAPS_LAST_REVIEW_DIAG_1":
                excelData.setCareGapsLastReviewDiag1((boolean) value);
                break;
            case "PATIENT_CONDITION_DIAG_1":
                String patientConditionDiag1 = (String) value;
                if (patientConditionDiag1.trim().isEmpty()) {
                    patientConditionDiag1 = "0";
                }
                excelData.setPatientConditionDiag1(patientConditionDiag1);
                break;
            case "PATIENT_CONDITION_DIAG_2":
                String patientConditionDiag2 = (String) value;
                if (patientConditionDiag2.trim().isEmpty()) {
                    patientConditionDiag2 = "0";
                }
                excelData.setPatientConditionDiag2(patientConditionDiag2);
                break;

            case "Referring facility":

                excelData.setFacilityName(removeTextAfterBrackets((String) value));
                break;

            default:
                break;
        }
    }



    public static LocalDate convertToDate(String dateStr) {
        List<String> patterns = List.of(
                "M/d/yy",
                "M/d/yyyy",
                "MM/dd/yy",
                "MM/dd/yyyy",
                "yyyy-MM-dd"
        );

        for (String pattern : patterns) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                return LocalDate.parse(dateStr, formatter);
            } catch (DateTimeParseException e) {
                // Try next pattern
            }
        }

        System.out.println("Unrecognized date format: " + dateStr);
        return null;
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    public static String setMonth(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate date = LocalDate.parse(dateString, formatter);
        return date.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH).toUpperCase();
    }

    public static int setYear(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate date = LocalDate.parse(dateString, formatter);
        return date.getYear();
    }


    /**
     * method to get cell value with type
     *
     * @param cell
     * @param columnDataType
     * @return
     * @author sowmiyathangaraj
     */
    private Object getCellValueWithType(Cell cell, String columnDataType) {
        String stringValue = "";
        try {
            if (cell == null) {
                return null;
            }
            stringValue = cell.toString().trim();

            // Clean string values with quotes
            if (stringValue.startsWith("\"") && stringValue.endsWith("\"")) {
                stringValue = stringValue.substring(1, stringValue.length() - 1);
            }

            switch (columnDataType.toLowerCase()) {
                case "string":
                    return stringValue;

                case "int":
                case "integer":

                    if (stringValue.contains(".")) {
                        stringValue = stringValue.split("\\.")[0];
                    }
                    try {
                        return Integer.parseInt(stringValue);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid integer format: " + stringValue);
                    }

                case "double":
                    try {
                        return Double.parseDouble(stringValue);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid double format: " + stringValue);
                    }

                case "boolean":
                    if (stringValue.equalsIgnoreCase("Yes")) {
                        return true;
                    } else {
                        return false;
                    }
                case "time":
                    if (DateUtil.isCellDateFormatted(cell)) {
                        // Get the date object from the cell, which contains both date and time
                        Date date = cell.getDateCellValue();

                        // Extract the time as a LocalTime object (only the time part)
//                        LocalTime time = LocalTime.of(
//                                date.getHours(),
//                                date.getMinutes(),
//                                date.getSeconds());

                        // Return the time as a formatted string or as a LocalTime object
//                        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
//                        return time.format(timeFormatter);  // Return time as string in "HH:mm:ss" format

                        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                        return formatter.format(date);
                    } else {
                        throw new IllegalArgumentException("Expected time format for cell");
                    }

                case "date":
                    if (DateUtil.isCellDateFormatted(cell)) {
                        Date date = cell.getDateCellValue();
                        return date.toInstant();
                    } else {
                        throw new IllegalArgumentException("Expected date format for cell");
                    }

                default:
                    throw new IllegalArgumentException("Unsupported column data type: " + columnDataType);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid data in cell: " + stringValue + " for type " + columnDataType);
        }
    }

    /**
     * method to get values from cell
     *
     * @author yogaraj
     */
    private String getCellStringValue(Cell cell) {
        if (cell == null) {
            return null; // or return a default value like ""
        }

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    // If the cell contains a date, format it as a string
                    Date date = cell.getDateCellValue();
                    yield new SimpleDateFormat("MM/dd/yyyy").format(date);
                } else if (cell.getCellStyle().getDataFormatString().contains("%")) {
                    // If the cell contains a percentage, multiply by 100 and append '%'
                    yield String.format("%.2f%%", cell.getNumericCellValue() * 100);
                } else {
                    // Otherwise, return the numeric value as a string
                    yield String.valueOf(cell.getNumericCellValue());
                }
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> String.valueOf(cell.getStringCellValue());
            default -> null; // Handle other types as necessary
        };
    }

    public static File convert(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

}
