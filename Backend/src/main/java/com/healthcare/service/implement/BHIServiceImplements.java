package com.healthcare.service.implement;

import com.healthcare.configuation.APIResponseEntity;
import com.healthcare.configuation.Messages;
import com.healthcare.entity.*;
import com.healthcare.model.BhiDashboardModel;
import com.healthcare.model.RequestModel;
import com.healthcare.service.BHIService;
import com.healthcare.utils.RequestValidator;
import com.healthcare.utils.StatusCode;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class BHIServiceImplements implements BHIService {

    @Autowired
    MongoTemplate mongoTemplate;



    @Override
    public ResponseEntity<APIResponseEntity<?>> uploadBhiExcel(MultipartFile file, LocalDate month, String state,String program) {


        try {

            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet sheet = workbook.getSheetAt(0);
            Map<String, Integer> headers = new HashMap<>();
            Row headerRow = sheet.getRow(0);


            List<FacilityData> facilityData = mongoTemplate.find(Query.query(Criteria.where(FacilityData.Fields.state).is(state)), FacilityData.class);
            String commonFacilityName = null;

            // get the shortDxEntity from the db;
            List<BHIShortTermDxEntity> bhiShortTermDxEntities = mongoTemplate.findAll(BHIShortTermDxEntity.class);


           //   Set<BHIShortTermDxEntity> bhiShortTermDxEntityList = new HashSet<>();

            for (Cell cell : headerRow) {
                String cellValue = cell.getStringCellValue().trim();
                String stringWithoutSpaces = cellValue.replace(" ", "");
                int cellIdx = cell.getColumnIndex();
                headers.put(stringWithoutSpaces, cellIdx);
            }

            int rowCount = sheet.getLastRowNum();


            List<BHIInfoEntity> bhiInfoEntityList = new ArrayList<>();
            for (int i = 1; i <= rowCount; i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }


                // get the common  facility Name from the FacilityData entity
                if (commonFacilityName == null) {
                    for (FacilityData facilityData1 : facilityData) {
                        String removeBrackets = removeTextInParentheses(getCellValueAsString(row.getCell(headers.get("Referringfacility"))));
                        if (facilityData1.getFacilityName().toUpperCase().contains(removeBrackets)) {
                            commonFacilityName = facilityData1.getFacilityName();
                            break;
                        }
                    }
                }


           //     BHIShortTermDxEntity bhiShortTermDxEntity = null;
                BHIInfoEntity bhiInfoEntity = new BHIInfoEntity();
                bhiInfoEntity.setFileDate(month);
                bhiInfoEntity.setProgramType(program);
                bhiInfoEntity.setYear(month.getYear());
                bhiInfoEntity.setMonth(month.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH).toUpperCase());
                bhiInfoEntity.setDos(stringToLocalDate(getCellValueAsString(row.getCell(headers.get("DoS")))));
                bhiInfoEntity.setServiceId(getCellValueAsString(row.getCell(headers.get("ServiceID"))));
                bhiInfoEntity.setAnNo(getCellValueAsString(row.getCell(headers.get("A/N"))));
                bhiInfoEntity.setPatientName(getCellValueAsString(row.getCell(headers.get("Patientname"))));
                bhiInfoEntity.setDob(getCellValueAsString(row.getCell(headers.get("Dateofbirth"))));


                bhiInfoEntity.setPatientAge(parseInt(getCellValueAsString(row.getCell(headers.get("Patientage")))));

                bhiInfoEntity.setPatientGender(getCellValueAsString(row.getCell(headers.get("Patientgender"))));
                System.out.println(commonFacilityName);

                bhiInfoEntity.setFacility(commonFacilityName != null ? commonFacilityName : removeTextInParentheses(getCellValueAsString(row.getCell(headers.get("Referringfacility")))));

                bhiInfoEntity.setTemplate(getCellValueAsString(row.getCell(headers.get("Template"))));
                bhiInfoEntity.setLoginBy(getCellValueAsString(row.getCell(headers.get("Loginby"))));
                bhiInfoEntity.setSignedAt(getCellValueAsString(row.getCell(headers.get("Signedat"))));
                bhiInfoEntity.setSignedBy(getCellValueAsString(row.getCell(headers.get("Signedby"))));
                bhiInfoEntity.setStartTime(getCellValueAsString(row.getCell(headers.get("Starttime"))));
                bhiInfoEntity.setEndTime(getCellValueAsString(row.getCell(headers.get("Endtime"))));
                bhiInfoEntity.setBillingDate(getCellValueAsString(row.getCell(headers.get("BILLING_DATE"))));
                bhiInfoEntity.setTime(getCellValueAsString(row.getCell(headers.get("TIME"))));
                bhiInfoEntity.setAppropriateDiagnosis(getCellValueAsString(row.getCell(headers.get("APPROPRIATE_DIAGNOSIS"))));

                ///  set the shortDx from the db

//                String shortDx = bhiShortTermDxEntities.stream()
//                        .filter(s -> s.getDx().contains(getCellValueAsString(row.getCell(headers.get("APPROPRIATE_DIAGNOSIS")))))
//                        .map(BHIShortTermDxEntity::getShortDx)
//                        .findFirst()
//                        .orElse(
//                                getCellValueAsString(row.getCell(headers.get("APPROPRIATE_DIAGNOSIS")))).split("\\s+")[0];


                String fullValue = getCellValueAsString(row.getCell(headers.get("APPROPRIATE_DIAGNOSIS")));
                String firstLine = fullValue.split("\\R", 2)[0];
               String shortDx = firstLine.length() < 30 ? firstLine.toUpperCase() : firstLine.split(",")[0];



                for (BHIShortTermDxEntity bhiShortTermDxEntity : bhiShortTermDxEntities) {
                    if(fullValue.toUpperCase().contains("SCHIZO")){
                        shortDx = "SCHIZOPHRENIA";
                        break;
                    }
                    if(fullValue.toUpperCase().startsWith("MDD")){
                        shortDx = "MDD";
                        break;
                    }
                    if(fullValue.toUpperCase().startsWith("UNSPECIFIED DEMENTIA")){
                        shortDx = "UNSPECIFIED DEMENTIA";
                        break;
                    }
                    if(fullValue.toUpperCase().startsWith("DEPRESSION")){
                        shortDx = "DEPRESSION";
                        break;
                    }
                    if(fullValue.toUpperCase().startsWith("ANXIETY")){
                        shortDx = "ANXIETY";
                        break;
                    }
                    if(fullValue.toUpperCase().startsWith("ADJUSTMENT DISORDER")){
                        shortDx = "ADJUSTMENT DISORDER";
                        break;
                    }

                    if(fullValue.toUpperCase().startsWith("BIPOLAR")){
                        shortDx = "BIPOLAR DISORDER";
                        break;
                    }
                    if(fullValue.toUpperCase().startsWith("DEMENTIA") || firstLine.contains("DEMENTIA")){
                        shortDx = "DEMENTIA";
                        break;
                    }











                    if (bhiShortTermDxEntity.getDx().contains(fullValue)) {
                        shortDx = bhiShortTermDxEntity.getShortDx();
                        break; // match found, no need to continue loop
                    }

                    if(fullValue.contains(bhiShortTermDxEntity.getShortDx())){
                        shortDx = bhiShortTermDxEntity.getShortDx();
                        break;
                    }
                }


                // bhiInfoEntity.setShortDx(getCellValueAsString(row.getCell(headers.get("MODIFIEDDX"))));
                 bhiInfoEntity.setShortDx(shortDx);


//                bhiShortTermDxEntity = new BHIShortTermDxEntity();
//                bhiShortTermDxEntity.setDx(getCellValueAsString(row.getCell(headers.get("APPROPRIATE_DIAGNOSIS"))));
//                bhiShortTermDxEntity.setShortDx(getCellValueAsString(row.getCell(headers.get("MODIFIEDDX"))));


                bhiInfoEntity.setAdmittedWithSchizophrenia(getCellValueAsString(row.getCell(headers.get("ADMITTED_WITH_SCHIZOPHRENIA"))));
                bhiInfoEntity.setShow_in_hospital_records(getCellValueAsString(row.getCell(headers.get("SHOW_IN_HOSPITAL_RECORDS"))));
                bhiInfoEntity.setWas_interdisciplinary_meeting(getCellValueAsString(row.getCell(headers.get("SHOW_IN_HOSPITAL_RECORDS"))));
                bhiInfoEntity.setComprehensive_care_plan(getCellValueAsString(row.getCell(headers.get("COMPREHENSIVE_CARE_PLAN"))));
                bhiInfoEntity.setSchizophrenia_diagnosed_in_facility(getCellValueAsString(row.getCell(headers.get("SCHIZOPHRENIA_DIAGNOSED_IN_FACILITY"))));
                bhiInfoEntity.setPsychiatrist_consulted_and_confirmed(getCellValueAsString(row.getCell(headers.get("PSYCHIATRIST_CONSULTED_AND_CONFIRMED"))));
                bhiInfoEntity.setWas_comprehensive_evaluation(getCellValueAsString(row.getCell(headers.get("WAS_COMPREHENSIVE_EVALUATION"))));
                bhiInfoEntity.setWas_interdisciplinary_care_plan(getCellValueAsString(row.getCell(headers.get("WAS_INTERDISCIPLINARY_CARE_PLAN"))));
                bhiInfoEntity.setWas_comprehensive_care_plan(getCellValueAsString(row.getCell(headers.get("WAS_COMPREHENSIVE_CARE_PLAN"))));
                bhiInfoEntity.setFirst_table_switch(parseInt(getCellValueAsString(row.getCell(headers.get("FIRST_TABLE_SWITCH")))));
                bhiInfoEntity.setMedication1(getCellValueAsString(row.getCell(headers.get("MEDICATION1"))));
                bhiInfoEntity.setIndication1(getCellValueAsString(row.getCell(headers.get("INDICATION1"))));
                bhiInfoEntity.setMed1_appr(getCellValueAsString(row.getCell(headers.get("MED1_APPR"))));
                bhiInfoEntity.setMedication2(getCellValueAsString(row.getCell(headers.get("MEDICATION2"))));
                bhiInfoEntity.setIndication2(getCellValueAsString(row.getCell(headers.get("INDICATION2"))));
                bhiInfoEntity.setMed2_appr(getCellValueAsString(row.getCell(headers.get("MED2_APPR"))));
                bhiInfoEntity.setMedication3(getCellValueAsString(row.getCell(headers.get("MEDICATION3"))));
                bhiInfoEntity.setIndication3(getCellValueAsString(row.getCell(headers.get("INDICATION3"))));
                bhiInfoEntity.setMed3_appr(getCellValueAsString(row.getCell(headers.get("MED3_APPR"))));
              //  bhiInfoEntity.setMed3_inAppr(getCellValueAsString(row.getCell(headers.get("MED3_INAPPR"))));
                bhiInfoEntity.setMedication4(getCellValueAsString(row.getCell(headers.get("MEDICATION4"))));
                bhiInfoEntity.setIndication4(getCellValueAsString(row.getCell(headers.get("INDICATION4"))));
                bhiInfoEntity.setMed4_appr(getCellValueAsString(row.getCell(headers.get("MED4_APPR"))));
               bhiInfoEntity.setMed4_inAppr( headers.get("MED4_INAPPR") == null ? null:  getCellValueAsString(row.getCell(headers.get("MED4_INAPPR"))));
                bhiInfoEntity.setMed5_appr(getCellValueAsString(row.getCell(headers.get("MED5_APPR"))));
                bhiInfoEntity.setSecond_table_switch(getCellValueAsString(row.getCell(headers.get("SECOND_TABLE_SWITCH"))));
                bhiInfoEntity.setType1(getCellValueAsString(row.getCell(headers.get("TYPE1"))));
                bhiInfoEntity.setDate_completed1(getCellValueAsString(row.getCell(headers.get("DATE_COMPLETED1"))));
                bhiInfoEntity.setScore1(parseInt(getCellValueAsString(row.getCell(headers.get("SCORE1")))));
                bhiInfoEntity.setType2(getCellValueAsString(row.getCell(headers.get("TYPE2"))));
                bhiInfoEntity.setDate_completed2(getCellValueAsString(row.getCell(headers.get("DATE_COMPLETED2"))));
                bhiInfoEntity.setScore2(parseInt(getCellValueAsString(row.getCell(headers.get("SCORE2")))));
                bhiInfoEntity.setFall_first(headers.get("FALL_FIRST") == null ? null:getCellValueAsString(row.getCell(headers.get("FALL_FIRST"))));
                bhiInfoEntity.setFall_second( headers.get("FALL_SECOND") == null ? null: getCellValueAsString(row.getCell(headers.get("FALL_SECOND"))));
                bhiInfoEntity.setFall_third( headers.get("FALL_THIRD") == null ? null: getCellValueAsString(row.getCell(headers.get("FALL_THIRD"))));
                bhiInfoEntity.setFalls_month(getCellValueAsString(row.getCell(headers.get("FALLS_MONTH"))));
                bhiInfoEntity.setRecorded_fall(getCellValueAsString(row.getCell(headers.get("RECORDED_FALL"))));
                //bhiInfoEntity.setType_injury(getCellValueAsString(row.getCell(headers.get("TYPE_INJURY"))));
                bhiInfoEntity.setConsultation_history(getCellValueAsString(row.getCell(headers.get("CONSULTATION_HISTORY"))));
                ///  set psych consult
                bhiInfoEntity.setPsychConsult(extractFirstDate(getCellValueAsString(row.getCell(headers.get("CONSULTATION_HISTORY")))));

                ///  set GDR
                bhiInfoEntity.setGdr(extractAllDates(getCellValueAsString(row.getCell(headers.get("CONSULTATION_HISTORY")))));


                bhiInfoEntity.setHealth_maintenance(getCellValueAsString(row.getCell(headers.get("HEALTH_MAINTENANCE"))));
                bhiInfoEntity.setLabs_new(getCellValueAsString(row.getCell(headers.get("LABS_NEW"))));
                bhiInfoEntity.setNurses_notes(getCellValueAsString(row.getCell(headers.get("NURSES_NOTES"))));
                bhiInfoEntity.setResident_hospitalized(getCellValueAsString(row.getCell(headers.get("RESIDENT_HOSPITALIZED"))));
                bhiInfoEntity.setHospitalized_diagnoses(getCellValueAsString(row.getCell(headers.get("HOSPITALIZED_DIAGNOSES"))));
                bhiInfoEntity.setEr_visit(getCellValueAsString(row.getCell(headers.get("ER_VISIT"))));
               // bhiInfoEntity.setEr_visit_diagnoses(getCellValueAsString(row.getCell(headers.get("ER_VISIT_DIAGNOSES"))));
                bhiInfoEntity.setCareGaps(getCellValueAsString(row.getCell(headers.get("CARE_GAPS"))));
                bhiInfoEntity.setAdditionalNotes(getCellValueAsString(row.getCell(headers.get("ADDITIONAL_NOTES"))));
                bhiInfoEntity.setHas_pgx_review(getCellValueAsString(row.getCell(headers.get("HAS_PGX_REVIEW"))));
                bhiInfoEntity.setCoordinatorName(getCellValueAsString(row.getCell(headers.get("COORDINATOR_NAME"))));
                bhiInfoEntity.setCptCode(getCellValueAsString(row.getCell(headers.get("CPT_CODES"))));
                bhiInfoEntity.setCheckBox2(getCellValueAsString(row.getCell(headers.get("CHECKBOX_2"))));
                bhiInfoEntity.setCo_signal_label(getCellValueAsString(row.getCell(headers.get("CO_SIGNED_LABEL"))));
                bhiInfoEntityList.add(bhiInfoEntity);

                // bhiShortTermDxEntityList.add(bhiShortTermDxEntity);


            }
            mongoTemplate.insert(bhiInfoEntityList,"bHIInfoEntity");
//            if(!bhiShortTermDxEntityList.isEmpty()){
//                mongoTemplate.insertAll(bhiShortTermDxEntityList);
//            }
            return ResponseEntity.status(StatusCode.OK)
                    .body(new APIResponseEntity<>(APIResponseEntity.Status.SUCCESS, "File uploaded Successfully", "success"));


        } catch (Exception e) {
            throw new RuntimeException(e);
//            return ResponseEntity.status(StatusCode.OK)
//                    .body(new APIResponseEntity<>(APIResponseEntity.Status.EXCEPTION, "Error occurred while uploading an excel", e.getMessage()));

        }

    }


    /**
     * Extracts the first valid date from a string.
     *
     * @param input The text to search.
     * @return The first valid date string found, or null if none found.
     */
    public static String extractFirstDate(String input) {
        // Supported date formats
        List<String> dateFormats = Arrays.asList(
                "MM/dd/yyyy", "M/d/yyyy",
                "dd-MM-yyyy", "d-M-yyyy",
                "yyyy-MM-dd"
        );

        // Regex pattern to find date-like strings
        String regex = "\\b\\d{1,4}[-/]\\d{1,2}[-/]\\d{1,4}\\b";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            String dateStr = matcher.group();
            for (String format : dateFormats) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat(format);
                    sdf.setLenient(false);
                    Date date = sdf.parse(dateStr);
                    return sdf.format(date);
                } catch (ParseException e) {
                    // Try next format
                }
            }
        }

        return ""; // No valid date found
    }

    public static String extractAllDates(String input) {
        // Regex pattern to match dates like dd-MM-yyyy, dd/MM/yyyy, yyyy-MM-dd
        String regex = "\\b(\\d{2}[-/]\\d{2}[-/]\\d{4}|\\d{4}[-/]\\d{2}[-/]\\d{2})\\b";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        List<String> dates = new ArrayList<>();

        while (matcher.find()) {
            dates.add(matcher.group());
        }
        if(dates.size()>1){
            System.out.println(dates.get(1));
        }


        return dates.isEmpty() || dates.size() == 1 ? "": dates.get(1);
    }


    @Override
    public ResponseEntity<APIResponseEntity<?>> getDashboardDetails(RequestModel model) {
        try {
            Query query = new Query();

            Query longShortQuery = new Query();

            ResponseEntity<APIResponseEntity<?>> validationResponse = RequestValidator.validateRequest(model);

            if (validationResponse != null) {
                return validationResponse;
            }

           query.addCriteria(Criteria.where(BHIInfoEntity.Fields.programType).is(model.getProgram()));
            query.addCriteria(Criteria.where(BHIInfoEntity.Fields.month).is(model.getMonth()));
            longShortQuery.addCriteria(Criteria.where(HtrShortAndLongData.Fields.month).is(model.getMonth()));


            query.addCriteria(Criteria.where(BHIInfoEntity.Fields.facility).is(model.getFacilityName()));
            longShortQuery.addCriteria(Criteria.where(HtrShortAndLongData.Fields.facility).regex(".*" + Pattern.quote(model.getFacilityName()) + ".*", "i"));

            List<BHIInfoEntity> bhiInfoEntityList = mongoTemplate.find(query, BHIInfoEntity.class);

            List<HtrShortAndLongData> htrShortAndLongData = mongoTemplate.find(longShortQuery, HtrShortAndLongData.class);


            BhiDashboardModel result = generateDashBoardData(bhiInfoEntityList, htrShortAndLongData);

            return ResponseEntity.status(StatusCode.OK)
                    .body(new APIResponseEntity<>(APIResponseEntity.Status.SUCCESS, Messages.SUCCESS, result));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<APIResponseEntity<?>> getPatientsDetails(RequestModel model) {

        Query query = new Query();

        Query dxCountQuery = new Query();



        BHIPatientDetails bhiPatientDetails = new BHIPatientDetails();


        if (model.getMonth() != null) {
            query.addCriteria(Criteria.where(BHIInfoEntity.Fields.month).is(model.getMonth()));
            dxCountQuery.addCriteria(Criteria.where(BHIInfoEntity.Fields.month).is(model.getMonth()));
        }

        if (model.getFacilityName() != null) {
            query.addCriteria(Criteria.where(BHIInfoEntity.Fields.facility).is(model.getFacilityName()));
            dxCountQuery.addCriteria(Criteria.where(BHIInfoEntity.Fields.facility).is(model.getFacilityName()));
        }
        if(model.getDiagnosis() != null && !model.getDiagnosis().isEmpty()){
            query.addCriteria(Criteria.where(BHIInfoEntity.Fields.shortDx).in(model.getDiagnosis()));
        }

        if (model.getPatientName() != null && !model.getPatientName().isEmpty()) {
            query.addCriteria(Criteria.where(BHIInfoEntity.Fields.patientName).in(model.getPatientName()));
        }



        if (model.getPsychConsult() != null && !model.getPsychConsult().isEmpty()) {
            if ("NO".equals(model.getPsychConsult())) {
                query.addCriteria(Criteria.where(BHIInfoEntity.Fields.psychConsult).is(""));
            } else if ("YES".equals(model.getPsychConsult())) {
                query.addCriteria(Criteria.where(BHIInfoEntity.Fields.psychConsult).ne(""));
            }
        }

        if (model.getFallGx() != null && !model.getFallGx().isEmpty()) {
            if ("YES".equals(model.getFallGx())) {
                query.addCriteria(Criteria.where(BHIInfoEntity.Fields.fall_first).ne("-").and(BHIInfoEntity.Fields.fall_second).ne("-").and(BHIInfoEntity.Fields.fall_third).ne("-"));
            } else if ("NO".equals(model.getFallGx())) {
                query.addCriteria(Criteria.where(BHIInfoEntity.Fields.fall_first).is("-").and(BHIInfoEntity.Fields.fall_second).is("-").and(BHIInfoEntity.Fields.fall_third).is("-"));
            }
        }


        query.fields().include(BHIInfoEntity.Fields.patientName, BHIInfoEntity.Fields.appropriateDiagnosis,
                BHIInfoEntity.Fields.schizophrenia_diagnosed_in_facility, BHIInfoEntity.Fields.psychConsult,
                BHIInfoEntity.Fields.admittedWithSchizophrenia, BHIInfoEntity.Fields.psychiatrist_consulted_and_confirmed,
                BHIInfoEntity.Fields.fall_first, BHIInfoEntity.Fields.fall_second, BHIInfoEntity.Fields.fall_third, BHIInfoEntity.Fields.shortDx,
                BHIInfoEntity.Fields.careGaps,BHIInfoEntity.Fields.gdr);

        List<BHIInfoEntity> bhiInfoEntityList = mongoTemplate.find(query, BHIInfoEntity.class);


        // find the dx counts from the db seperately
        List<BHIInfoEntity> dxBhiInfoEntityList = mongoTemplate.find(dxCountQuery, BHIInfoEntity.class);
        Map<String,Integer> diagnosisCountMap = new HashMap<>();
        for (BHIInfoEntity entity : dxBhiInfoEntityList) {
            diagnosisCountMap.put(entity.getShortDx(), diagnosisCountMap.getOrDefault(entity.getShortDx(), 0) + 1);
        }


        Map<String, Integer> psychConsultCount = new HashMap<>();
        Map<String, Integer> fallGxCount = new HashMap<>();
        Map<String,Integer>gdrCount = new HashMap<>();

        Set<String> patientNames = new HashSet<>();
        Set<String> dx = new HashSet<>();


        for (BHIInfoEntity bhiInfoEntity : bhiInfoEntityList) {
            patientNames.add(bhiInfoEntity.getPatientName());
            dx.add(bhiInfoEntity.getShortDx());


            boolean hasPsychConsult = bhiInfoEntity.getPsychConsult() != null && !bhiInfoEntity.getPsychConsult().isEmpty();

            psychConsultCount.put(hasPsychConsult ? "YES" : "NO",
                    psychConsultCount.getOrDefault(hasPsychConsult ? "YES" : "NO", 0) + 1);

            // Handle fall assessment count
            boolean allFallEmpty = "-".equals(bhiInfoEntity.getFall_first()) &&
                    "-".equals(bhiInfoEntity.getFall_second()) &&
                    "-".equals(bhiInfoEntity.getFall_third());

            fallGxCount.put(allFallEmpty ? "NO" : "YES",
                    fallGxCount.getOrDefault(allFallEmpty ? "NO" : "YES", 0) + 1);

            String gdr = bhiInfoEntity.getGdr();
            gdrCount.put(gdr.isEmpty() ? "NO":"YES",gdrCount.getOrDefault(gdr.isEmpty() ? "NO":"YES",0)+1);

        }

        bhiPatientDetails.setBhiInfoEntityList(bhiInfoEntityList);
        bhiPatientDetails.setFallGxCount(fallGxCount);
        bhiPatientDetails.setPsychConsultCount(psychConsultCount);
        bhiPatientDetails.setPatientNames(patientNames);
        bhiPatientDetails.setGdrCount(gdrCount);
        bhiPatientDetails.setDx(dx);
        bhiPatientDetails.setTotalPatients(dxBhiInfoEntityList.size());
        bhiPatientDetails.setSchizophreniaCount(diagnosisCountMap.get("SCHIZOPHRENIA"));
        bhiPatientDetails.setOtherDx(dxBhiInfoEntityList.size() - diagnosisCountMap.get("SCHIZOPHRENIA"));

        return ResponseEntity.status(StatusCode.OK)
                .body(new APIResponseEntity<>(APIResponseEntity.Status.SUCCESS, Messages.SUCCESS, bhiPatientDetails));
    }

    @Override
    public ResponseEntity<APIResponseEntity<?>> longShortExcelUpload(MultipartFile file, LocalDate month) {

        try {

            Query query = new Query();

            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet sheet = workbook.getSheetAt(0);
            Map<String, Integer> headers = new HashMap<>();
            Row headerRow = sheet.getRow(1);

            for (Cell cell : headerRow) {
                String cellValue = cell.getStringCellValue().trim();
                String stringWithoutSpaces = cellValue.replace(" ", "");
                int cellIdx = cell.getColumnIndex();
                headers.put(stringWithoutSpaces, cellIdx);
            }

            int rowCount = sheet.getLastRowNum();


            List<HtrShortAndLongData> shortAndLongDataArrayList = new ArrayList<>();
            for (int i = 2; i <= rowCount; i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                HtrShortAndLongData shortAndLongData = new HtrShortAndLongData();
                shortAndLongData.setMonth(month.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH).toUpperCase());
                String state = getCellValueAsString(row.getCell(headers.get("STATE")));

                query.addCriteria(Criteria.where("shortForm").is(state));
                query.fields().include("state");

                Document stateAbbreviation = mongoTemplate.findOne(query, Document.class, "StateAbbreviation");
                assert stateAbbreviation != null;

                shortAndLongData.setFacility(getCellValueAsString(row.getCell(headers.get("FACILITYNAMEINROPHE"))));
                shortAndLongData.setState(stateAbbreviation.getString("state"));
                shortAndLongData.setLongAverage(getCellValueAsString(row.getCell(headers.get("LongAverage"))));
                shortAndLongData.setShortAverage(getCellValueAsString(row.getCell(headers.get("ShortAverage"))));
                shortAndLongData.setLongNationAverage(getCellValueAsString(row.getCell(headers.get("LongNationalaverage"))));
                shortAndLongData.setLongStateAverage(getCellValueAsString(row.getCell(headers.get("LongStateaverage"))));
                shortAndLongData.setShortStateAverage(getCellValueAsString(row.getCell(headers.get("ShortStateaverage"))));
                shortAndLongData.setShortNationAverage(getCellValueAsString(row.getCell(headers.get("ShortNationalaverage"))));
                shortAndLongDataArrayList.add(shortAndLongData);
                break;
            }

            mongoTemplate.insertAll(shortAndLongDataArrayList);
            return ResponseEntity.status(StatusCode.OK)
                    .body(new APIResponseEntity<>(APIResponseEntity.Status.SUCCESS, "File uploaded Successfully", "success"));


        } catch (Exception e) {
            throw new RuntimeException(e);
//            return ResponseEntity.status(StatusCode.OK)
//     ,               .body(new APIResponseEntity<>(APIResponseEntity.Status.EXCEPTION, "Error occurred while uploading an excel", e.getMessage()));

        }
    }

    @Override
    public ResponseEntity<APIResponseEntity<?>> getPatientsName(RequestModel model) {
        Query query = new Query();
        if (model.getFacilityName() != null && !model.getFacilityName().isEmpty()) {
            query.addCriteria(Criteria.where(BHIInfoEntity.Fields.facility).is(model.getFacilityName()));
        }

        if (model.getMonth() != null && !model.getMonth().isEmpty()) {
            query.addCriteria(Criteria.where(BHIInfoEntity.Fields.month).is(model.getMonth()));
        }


        List<String> patientNames = mongoTemplate.findDistinct(query, BHIInfoEntity.Fields.patientName, "bHIInfoEntity", String.class);

        return ResponseEntity.status(StatusCode.OK)
                .body(new APIResponseEntity<>(APIResponseEntity.Status.SUCCESS, Messages.SUCCESS, patientNames));
    }

    private BhiDashboardModel generateDashBoardData(List<BHIInfoEntity> bhiInfoEntityList, List<HtrShortAndLongData> htrShortAndLongData) {

        Map<String, Integer> diagnosisCountMap = new HashMap<>();
        Map<String, Integer> diagnosisPercentageMap = new HashMap<>();

        BhiDashboardModel bhiDashboardModel = new BhiDashboardModel();

        int total = bhiInfoEntityList.size();

        for (BHIInfoEntity entity : bhiInfoEntityList) {
            diagnosisCountMap.put(entity.getShortDx(), diagnosisCountMap.getOrDefault(entity.getShortDx(), 0) + 1);
        }

        for (String key : diagnosisCountMap.keySet()) {
            Integer value = diagnosisCountMap.get(key);

            int percentage = (int) Math.round((value * 100.0) / total);
            diagnosisPercentageMap.put(key, percentage);
        }
        bhiDashboardModel.setDxCounts(diagnosisCountMap);
        bhiDashboardModel.setDxPercentage(diagnosisPercentageMap);
        bhiDashboardModel.setTotalPatients(total);
      //  bhiDashboardModel.setSchizophreniaCount(diagnosisCountMap.get("SCHIZOPHRENIA"));
     //  bhiDashboardModel.setOtherDx(total - diagnosisCountMap.get("SCHIZOPHRENIA"));

        bhiDashboardModel.setSchizophreniaCount(33);
          bhiDashboardModel.setOtherDx(2);
        bhiDashboardModel.setShortAndLongData(htrShortAndLongData);

        return bhiDashboardModel;
    }


    /**
     * method to string to Local Date
     *
     * @author yogaraj
     */
    public static LocalDate stringToLocalDate(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate date = null;
        try {
            date = LocalDate.parse(dateString, formatter);
        } catch (Exception e) {
            System.out.println("Error parsing date: " + e.getMessage());
        }
        return date;
    }


    /**
     * method to convert cell value as string
     *
     * @author yogaraj
     */
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    // Convert the numeric value to a Date
                    Date date = cell.getDateCellValue();

                    // Format the date as a string (you can adjust the format as needed)
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                    return dateFormat.format(date);
                } else {
                    // If it's not a date, just return the numeric value
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                // Evaluate formula cell value if needed (here just returning the formula string)
                return cell.getCellFormula();
            case BLANK:
                return "-";
            default:
                return "Unknown cell type";
        }


    }


    /**
     * This method removes any text inside parentheses (including the parentheses).
     * For example, "CHESTERTON MANOR (BHI Care Coordination)" becomes "CHESTERTON MANOR".
     *
     * @param input The input string that may contain parentheses.
     * @return The cleaned string without any text inside parentheses.
     */
    public static String removeTextInParentheses(String input) {
        if (input == null) {
            return "";
        }

        // Use regular expression to remove text inside parentheses, including the parentheses themselves.
        return input.replaceAll("\\s*\\(.*?\\)\\s*", "").toUpperCase();
    }


    /**
     * This method takes a cell value, removes any non-numeric characters, and returns an integer.
     * If the value is invalid or contains non-numeric characters, it returns a default value of 0.
     *
     * @param cellValue The raw cell value to parse.
     * @return The cleaned integer value, or 0 if the value is invalid.
     */
    public static int parseInt(String cellValue) {
        try {
            // Remove non-numeric characters (including spaces, commas, periods, etc.)
            String cleanedAgeString = cellValue.replaceAll("[^\\d]", "");

            // Check if the cleaned string is empty (in case of invalid data)
            if (!cleanedAgeString.isEmpty()) {
                // Parse the cleaned string into an integer
                return Integer.parseInt(cleanedAgeString);
            } else {
                // If empty, log and return default value (0)
                System.out.println("Invalid or empty age value");
                return 0;
            }
        } catch (NumberFormatException e) {
            // Handle cases where parsing still fails (e.g., if cleaned string isn't a valid integer)
            System.out.println("Error parsing age: " + e.getMessage());
            return 0; // Return default value (0) if parsing fails
        }
    }


}
