package com.healthcare.service.implement;

import com.healthcare.configuation.APIResponseEntity;
import com.healthcare.configuation.Messages;
import com.healthcare.domain.Audit;
import com.healthcare.entity.*;
import com.healthcare.entity.Enum;
import com.healthcare.model.*;
import com.healthcare.service.ICCMService;
import com.healthcare.utils.StatusCode;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CCMServiceImp implements ICCMService {

    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * method to get ccm details
     *
     * @param model model
     * @return responseList
     * @author sowmiyathangaraj
     */
    @Override
    public List<CCMResponseModel> getCCMPopulationDetails(RequestModel model) {
        List<CCMResponseModel> responseList = new ArrayList<>();
        try {
            if (model != null && model.getYear() != 0 && !model.getMonth().isEmpty() && !model.getFacilityName().isEmpty()) {
                List<String> distinctPatientConditions = mongoTemplate.query(FileDataInfo.class)
                        .distinct(FileDataInfo.Fields.patientConditionDiag1)
                        .matching(Query.query(
                                Criteria.where(FileDataInfo.Fields.facilityName).is(model.getFacilityName())
                                        .and(FileDataInfo.Fields.month).is(model.getMonth())
                                        .and(FileDataInfo.Fields.programType).is(Enum.CCM))).as(String.class).all();

                if (!distinctPatientConditions.isEmpty()) {
                    Query query = new Query();
                    query.addCriteria(Criteria.where(FileDataInfo.Fields.patientConditionDiag1).in(distinctPatientConditions));

                    // Specify the fields you need to retrieve
                    query.fields().include(FileDataInfo.Fields.patientName)
                            .include(FileDataInfo.Fields.diagnosList)
                            .include(FileDataInfo.Fields.careGaps)
                            .include(FileDataInfo.Fields.patientConditionDiag1);

                    // Step 3: Execute the query
                    List<FileDataInfo> ccmPopulateDetails = mongoTemplate.find(query, FileDataInfo.class);
                    if (!ccmPopulateDetails.isEmpty()) {

                        for (FileDataInfo data : ccmPopulateDetails) {
                            CCMResponseModel response = new CCMResponseModel();
                            response.setPatientName(data.getPatientName());
                            response.setCareGaps(data.getCareGaps());
                            response.setDiagnosList(data.getDiagnosList());
                            response.setPatientConditionDiag1(data.getPatientConditionDiag1());
                            responseList.add(response);
                        }

                        return responseList;
                    }
                } else {
                    throw new RuntimeException("Failed to processing");
                }

            } else {
                throw new RuntimeException("Invalid Parameters");
            }

        } catch (RuntimeException e) {
            e.printStackTrace();

        }
        return responseList;
    }


    /**
     * method to get diagnosis counts from the database
     *
     * @param model
     * @return diagnosisCountMap
     * @author sowmiyathangaraj
     */
    @Override
    public Map<String, Integer> getDiagnosisCounts(RequestModel model) {
        Map<String, Integer> diagnosisCountMap = new HashMap<>();
        try {
            if (model != null && !model.getMonth().isEmpty() && model.getYear() != 0 && !model.getFacilityName().isEmpty()) {
                // to get records by request using match operation
                MatchOperation matchOperation = Aggregation.match(Criteria.where(FileDataInfo.Fields.facilityName).is(model.getFacilityName())
                        .and(FileDataInfo.Fields.month).is(model.getMonth())
                        .and(FileDataInfo.Fields.programType).is(Enum.CCM));

                // to get the records using grouping by diagnosis list
                GroupOperation groupOperation = Aggregation.group(FileDataInfo.Fields.diagnosList)
                        .count().as("count");

                // to execute this operation using aggregation
                Aggregation aggregation = Aggregation.newAggregation(matchOperation, groupOperation);

                // execute the query
                List<Map> values = mongoTemplate.aggregate(aggregation, FileDataInfo.class, Map.class).getMappedResults();

                // Check for null and create response
                if (values.isEmpty()) {
                    throw new RuntimeException("No diagnosis counts found");
                }
                // Create a map to hold the diagnosis counts

                for (Map value : values) {
                    String diagnosis = (String) value.get("_id");
                    Integer count = (Integer) value.get("count");
                    diagnosisCountMap.put(diagnosis, count);
                }

                return diagnosisCountMap;

            } else {
                throw new RuntimeException("Invalid Parameters");
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return diagnosisCountMap;


    }

    /**
     * method to get patient condition list from the database
     *
     * @return distinctPatientConditions
     * @author sowmiyathangaraj
     */
    @Override
    public List<String> getPatientConditionList(RequestModel model) {
        List<String> distinctPatientConditions = new ArrayList<>();
        try {
            if (model != null && model.getMonth() != null && !model.getFacilityName().isEmpty()) {
                distinctPatientConditions = mongoTemplate.query(FileDataInfo.class)
                        .distinct(FileDataInfo.Fields.patientConditionDiag1)
                        .matching(Query.query(
                                Criteria.where(FileDataInfo.Fields.facilityName).is(model.getFacilityName())
                                        .and(FileDataInfo.Fields.month).is(model.getMonth())
                                        .and(FileDataInfo.Fields.programType).is(Enum.CCM))).as(String.class).all();
                if (distinctPatientConditions.isEmpty()) {
                    throw new RuntimeException("No data found");
                }
                return distinctPatientConditions;

            } else {
                throw new RuntimeException("Invalid Parameters");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return distinctPatientConditions;
    }


    /**
     * method to get immunization details for CCM from database
     *
     * @return response
     * @author sowmiyathangaraj
     */
    @Override
    public Map<String, Map<String, Integer>> getImmunizationDetails(RequestModel model) {
        Map<String, Map<String, Integer>> response = new HashMap<>();
        MatchOperation matchOperation = null;
        try {
            // validate user input
            if (model != null && model.getMonth() != null && !model.getFacilityName().isEmpty()) {

                // Define the fields
                String[] fields = {
                        Enum.pneumococcal.name(),
                        Enum.influenza.name(),
                        Enum.prevnar.name(),
                        Enum.covidVaccine1.name(),
                        Enum.covidVaccine2.name(),
                        Enum.covidBooster.name()
                };

                if (model.getPatientName() != null && !model.getPatientName().isEmpty()) {
                    // Match operation to filter the values based on user input
                    matchOperation = Aggregation.match(
                            Criteria.where(FileDataInfo.Fields.facilityName).is(model.getFacilityName())
                                    .and(FileDataInfo.Fields.month).is(model.getMonth())
                                    .and(FileDataInfo.Fields.patientName).in(model.getPatientName())
                                    .and(FileDataInfo.Fields.programType).is(Enum.CCM)
                    );
                } else {
                    // Match operation to filter the values  based on user input if not present in patient name
                    matchOperation = Aggregation.match(
                            Criteria.where(FileDataInfo.Fields.facilityName).is(model.getFacilityName())
                                    .and(FileDataInfo.Fields.month).is(model.getMonth())
                                    .and(FileDataInfo.Fields.programType).is(Enum.CCM)
                    );
                }

                for (String field : fields) {
                    // Group by the field and count occurrences
                    GroupOperation groupOperation = Aggregation.group(field).count().as("count");

                    // Aggregation pipeline with match stage
                    Aggregation aggregation = Aggregation.newAggregation(matchOperation, groupOperation);

                    // Execute the aggregation on the FileDataInfo collection
                    AggregationResults<CCMResponseModel> results = mongoTemplate.aggregate(aggregation, FileDataInfo.class, CCMResponseModel.class);


                    Map<String, Integer> fieldCounts = new HashMap<>();

                    // Process the results
                    List<CCMResponseModel> mappedResults = results.getMappedResults();
                    for (CCMResponseModel result : mappedResults) {
                        fieldCounts.put(result.getId(), result.getCount());
                    }

                    response.put(field, fieldCounts);
                }
            } else {
                throw new RuntimeException("Invalid Parameters");
            }

            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * method to get single immunization details from database
     *
     * @param model model
     * @return response
     * @author sowmiyathangaraj
     */
    @Override
    public List<CCMResponseModel> singleImmunizationDetails(RequestModel model) {
        List<CCMResponseModel> response = new ArrayList<>();
        try {
            // Validate user input
            if (model != null && model.getFacilityName() != null && model.getMonth() != null && model.getImmunizationName() != null) {

                // Match operation to filter based on reportDate, facilityName, and optionally patientName
                MatchOperation matchOperation = Aggregation.match(
                        Criteria.where(FileDataInfo.Fields.facilityName).is(model.getFacilityName())
                                .and(FileDataInfo.Fields.month).is(model.getMonth())
                );


                if (model.getPatientName() != null && !model.getPatientName().isEmpty()) {
                    matchOperation = Aggregation.match(
                            Criteria.where(FileDataInfo.Fields.facilityName).is(model.getFacilityName())
                                    .and(FileDataInfo.Fields.month).is(model.getMonth())
                                    .and(FileDataInfo.Fields.patientName).in(model.getPatientName())
                    );
                }

                // Projection to include only the fields patientName and user-specified immunization column
                ProjectionOperation projectionOperation = Aggregation.project(FileDataInfo.Fields.patientName, model.getImmunizationName());


                // Build aggregation
                Aggregation aggregation = Aggregation.newAggregation(matchOperation, projectionOperation);


                // Execute the aggregation
                AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, FileDataInfo.class, Document.class);


                // Process the results
                for (Document document : results) {
                    CCMResponseModel responseModel = new CCMResponseModel();
                    responseModel.setPatientName(document.getString(FileDataInfo.Fields.patientName));
                    responseModel.setImmunizationName(document.getString(model.getImmunizationName()));
                    response.add(responseModel);
                }


                if (!model.getImmunizationCondition().isEmpty()) {
                    response = response.stream()
                            .filter(i -> model.getImmunizationCondition().contains(i.getImmunizationName()))
                            .collect(Collectors.toList());
                }
                if (!response.isEmpty()) {
                    return response;
                }
            } else {
                throw new RuntimeException("Invalid Input Parameters");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }


    /**
     * method to get assessment datails from database
     *
     * @param model model
     * @return response
     * @author sowmiyathangaraj
     */
    @Override
    public Map<String, Map<String, Integer>> getAssessmentDetails(RequestModel model) {
        Map<String, Map<String, Integer>> response = new HashMap<>();
        MatchOperation matchOperation = null;
        try {
            // validate user input
            if (model != null && model.getMonth() != null && !model.getFacilityName().isEmpty()) {

                // Define the fields
                String[] fields = {
                        Enum.advanceDirective.name(),
                        Enum.advanceDirectivesTypes.name(),
                        Enum.advCarePlan.name(),
                        Enum.advDoctorsOrder.name(),
                        Enum.catheter.name(),
                        Enum.catheterType.name(),
                        Enum.catheterCarePlan.name(),
                        Enum.catheterIndication.name(),
                        Enum.fallRiskAssessment.name(),
                        Enum.fallRiskCarePlan.name(),
                        Enum.skinRiskAssessment.name(),
                        Enum.skinRiskCategory.name(),
                        Enum.skinRiskStore.name(),
                        Enum.skinRiskCarePlan.name()
                };
                int age = model.getAge();

                if (model.getPatientName() != null && !model.getPatientName().isEmpty()) {

                    if (age > 0) {
                        matchOperation = Aggregation.match(
                                Criteria.where(FileDataInfo.Fields.facilityName).is(model.getFacilityName())
                                        .and(FileDataInfo.Fields.month).is(model.getMonth())
                                        .and(FileDataInfo.Fields.programType).is(Enum.CCM)
                                        .and(FileDataInfo.Fields.patientName).in(model.getPatientName())
                                        .and(FileDataInfo.Fields.patientAge).gte(age) // Filtering based on age
                        );
                    } else {
                        // Match operation to filter the values based on user input
                        matchOperation = Aggregation.match(
                                Criteria.where(FileDataInfo.Fields.facilityName).is(model.getFacilityName())
                                        .and(FileDataInfo.Fields.month).is(model.getMonth())
                                        .and(FileDataInfo.Fields.patientName).in(model.getPatientName())
                                        .and(FileDataInfo.Fields.programType).is(Enum.CCM)

                        );
                    }
                } else {
                    if (age > 0) {
                        matchOperation = Aggregation.match(
                                Criteria.where(FileDataInfo.Fields.facilityName).is(model.getFacilityName())
                                        .and(FileDataInfo.Fields.month).is(model.getMonth())
                                        .and(FileDataInfo.Fields.programType).is(Enum.CCM)
                                        .and(FileDataInfo.Fields.patientAge).gte(age) // Filtering based on age
                        );
                    } else {
                        matchOperation = Aggregation.match(
                                Criteria.where(FileDataInfo.Fields.facilityName).is(model.getFacilityName())
                                        .and(FileDataInfo.Fields.month).is(model.getMonth())
                                        .and(FileDataInfo.Fields.programType).is(Enum.CCM)
                        );
                    }
                }


                for (String field : fields) {
                    // Group by the field and count occurrences
                    GroupOperation groupOperation = Aggregation.group(field).count().as("count");

                    // Aggregation pipeline with match stage
                    Aggregation aggregation = Aggregation.newAggregation(matchOperation, groupOperation);

                    // Execute the aggregation on the FileDataInfo collection
                    AggregationResults<CCMResponseModel> results = mongoTemplate.aggregate(aggregation, FileDataInfo.class, CCMResponseModel.class);


                    Map<String, Integer> fieldCounts = new HashMap<>();

                    // Process the results
                    List<CCMResponseModel> mappedResults = results.getMappedResults();
                    for (CCMResponseModel result : mappedResults) {
                        fieldCounts.put(result.getId(), result.getCount());
                    }
                    response.put(field, fieldCounts);
                }
            } else {
                throw new RuntimeException("Invalid Parameters");
            }

            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * method to get single assessment details from database
     *
     * @param model model
     * @return response
     * @author sowmiyathangaraj
     */
    @Override
    public List<CCMResponseModel> singleAssessmentDetails(RequestModel model) {
        List<CCMResponseModel> response = new ArrayList<>();
        try {
            // Validate user input
            if (model != null && model.getFacilityName() != null && model.getMonth() != null && model.getAssessmentName() != null) {

                // Match operation to filter based on reportDate, facilityName, and optionally patientName
                MatchOperation matchOperation = Aggregation.match(
                        Criteria.where(FileDataInfo.Fields.facilityName).is(model.getFacilityName())
                                .and(FileDataInfo.Fields.month).is(model.getMonth())
                );
                if (model.getPatientName() != null && !model.getPatientName().isEmpty()) {
                    matchOperation = Aggregation.match(
                            Criteria.where(FileDataInfo.Fields.facilityName).is(model.getFacilityName())
                                    .and(FileDataInfo.Fields.month).is(model.getMonth())
                                    .and(FileDataInfo.Fields.patientName).in(model.getPatientName())
                    );
                }

                // Projection to include only the fields patientName and user-specified immunization column
                ProjectionOperation projectionOperation = Aggregation.project(FileDataInfo.Fields.patientName, model.getAssessmentName());

                // Build aggregation
                Aggregation aggregation = Aggregation.newAggregation(matchOperation, projectionOperation);

                // Execute the aggregation
                AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, FileDataInfo.class, Document.class);


                // Process the results
                for (Document document : results) {
                    CCMResponseModel responseModel = new CCMResponseModel();
                    responseModel.setPatientName(document.getString(FileDataInfo.Fields.patientName));
                    responseModel.setAssessmentName(document.getString(model.getAssessmentName()));
                    response.add(responseModel);
                }
                if (model.getPatientCondition() != null && !model.getPatientCondition().isEmpty()) {
                    response = response.stream()
                            .filter(i -> model.getPatientCondition().contains(i.getAssessmentName()))
                            .collect(Collectors.toList());
                }
                if (!response.isEmpty()) {
                    return response;
                }
            } else {
                throw new RuntimeException("Invalid Input Parameters");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;

    }

    /**
     * method to get patient information by patient conditions
     *
     * @param model
     * @return
     * @author sowmiyathangaraj
     */

    @Override
    public List<CCMResponseModel> getPatientInfoByPatientCondition(RequestModel model) {
        List<CCMResponseModel> response = new ArrayList<>();
        try {
            if (model != null && !model.getPatientCondition().isEmpty()
                    && model.getFacilityName() != null && model.getMonth() != null) {

                // Match operation to filter based on reportDate, facilityName, and optionally patientName
                MatchOperation matchOperation = Aggregation.match(
                        Criteria.where(FileDataInfo.Fields.facilityName).is(model.getFacilityName())
                                .and(FileDataInfo.Fields.month).is(model.getMonth())
                                .and(FileDataInfo.Fields.programType).is(Enum.CCM)
                                .and(FileDataInfo.Fields.patientConditionDiag1).in(model.getPatientCondition())

                );
                if (model.getPatientName() != null && !model.getPatientName().isEmpty()) {
                    matchOperation = Aggregation.match(
                            Criteria.where(FileDataInfo.Fields.facilityName).is(model.getFacilityName())
                                    .and(FileDataInfo.Fields.month).is(model.getMonth())
                                    .and(FileDataInfo.Fields.patientName).in(model.getPatientName())
                                    .and(FileDataInfo.Fields.programType).is(Enum.CCM)
                                    .and(FileDataInfo.Fields.patientConditionDiag1).in(model.getPatientCondition())
                    );
                }

                // Projection to include only the fields patientName and user-specified immunization column
                ProjectionOperation projectionOperation = Aggregation.project(FileDataInfo.Fields.patientName, FileDataInfo.Fields.careGaps);

                // Build aggregation
                Aggregation aggregation = Aggregation.newAggregation(matchOperation, projectionOperation);

                // Execute the aggregation
                AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, FileDataInfo.class, Document.class);

                // Process the results
                for (Document document : results) {
                    CCMResponseModel responseModel = new CCMResponseModel();
                    responseModel.setPatientName(document.getString(FileDataInfo.Fields.patientName));
                    responseModel.setCareGaps(document.getString(FileDataInfo.Fields.careGaps));
                    response.add(responseModel);
                }
                if (!response.isEmpty()) {
                    return response;
                }
            } else {
                throw new RuntimeException("Invalid Parameters");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return response;
    }

    @Override
    public List<PatientDetails> getPatientDetails(RequestModel model) {

        try {
            Query query = new Query();
//            if(!model.getPatientName().isEmpty()){
//                query.addCriteria(Criteria.where(MedEliteReports.Fields.patientName).is(model.getPatientName()));
//            }
            query.addCriteria(Criteria.where(MedEliteReports.Fields.facilityName).is(model.getFacilityName()));
            query.addCriteria(Criteria.where(MedEliteReports.Fields.month).is(model.getMonth()));
            query.addCriteria(Criteria.where(MedEliteReports.Fields.year).is(model.getYear()));
            List<MedEliteReports> medEliteReportsList = mongoTemplate.find(query, MedEliteReports.class);
            List<PatientDetails> patientDetails = new ArrayList<>();


            for (MedEliteReports medEliteReports : medEliteReportsList) {
                PatientDetails patientDetailsObj = getPatientDetails(medEliteReports);
                patientDetails.add(patientDetailsObj);
            }

            return patientDetails;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Document> getDiagnosisListCount(RequestModel model) {
        List<AggregationOperation> aggregationOperations = new ArrayList<>();
        try {
            //  if (model != null && model.get() != null && !model.getFacilityName().isEmpty()) {
            if (model.getPatientName() != null && !model.getPatientName().isEmpty()) {
                aggregationOperations.add(Aggregation.match(Criteria.where(FileDataInfo.Fields.patientName).in(model.getPatientName())));
            }
            if (model.getPatientCondition() != null && !model.getPatientCondition().isEmpty()) {
                aggregationOperations.add(Aggregation.match(Criteria.where(FileDataInfo.Fields.patientConditionDiag1).is(model.getPatientCondition())));
            }
            if (model.getAge() != 0) {
                aggregationOperations.add(Aggregation.match(Criteria.where(FileDataInfo.Fields.patientAge).gte(model.getAge())));
            }
            aggregationOperations.add(Aggregation.match(Criteria.where(FileDataInfo.Fields.year).is(model.getYear())));
            aggregationOperations.add(Aggregation.match(Criteria.where(FileDataInfo.Fields.month).is(model.getMonth())));
            aggregationOperations.add(Aggregation.match(Criteria.where(FileDataInfo.Fields.facilityName).is(model.getFacilityName())));
            aggregationOperations.add(Aggregation.group(FileDataInfo.Fields.diagnosList)
                    .count().as("diagnosListCount")
            );
            aggregationOperations.add(Aggregation.project("diagnosListCount")
                    .and("_id").as("diagnosList")
                    .andExclude("_id")
            );
            Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);
            List<Document> result = mongoTemplate.aggregate(aggregation, "fileDataInfo", Document.class).getMappedResults();
            return result;
            // }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public ResponseEntity<APIResponseEntity<?>> getConditionCareGapCount(RequestModel model) {

        List<Document> populationResult = getDiagnosisListCount(model);

        try {
            List<AggregationOperation> aggregationOperations = new ArrayList<>();
            if (model.getPatientName() != null && !model.getPatientName().isEmpty()) {
                aggregationOperations.add(Aggregation.match(Criteria.where(FileDataInfo.Fields.patientName).is(model.getPatientName())));
            }
            if (model.getPatientCondition() != null && !model.getPatientCondition().isEmpty()) {
                aggregationOperations.add(Aggregation.match(Criteria.where(FileDataInfo.Fields.patientConditionDiag1).is(model.getPatientCondition())));
            }
            if (model.getAge() != 0) {
                aggregationOperations.add(Aggregation.match(Criteria.where(FileDataInfo.Fields.patientAge).gte(model.getAge())));
            }


            aggregationOperations.add(Aggregation.match(Criteria.where(FileDataInfo.Fields.year).is(model.getYear())));
            aggregationOperations.add(Aggregation.match(Criteria.where(FileDataInfo.Fields.month).is(model.getMonth())));
            aggregationOperations.add(Aggregation.match(Criteria.where(FileDataInfo.Fields.facilityName).is(model.getFacilityName())));
            aggregationOperations.add(Aggregation.group(FileDataInfo.Fields.diagnosList)
                    .push(FileDataInfo.Fields.patientConditionDiag1).as("conditionCareGap")
                    .push(Aggregation.ROOT).as("patientDetails")
            );

            aggregationOperations.add(Aggregation.project("conditionCareGap", "patientDetails")
                    .and("_id").as(FileDataInfo.Fields.diagnosList)
                    .andExclude("_id")
            );


            Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);
            List<Document> result = mongoTemplate.aggregate(aggregation, "fileDataInfo", Document.class).getMappedResults();
            List<DiagnosListCount> diagnosListCountList = new ArrayList<>();

            for (Document document : result) {
                String diagnosList = document.getString("diagnosList");

                List<FileDataInfo> patientDetailsList = (List<FileDataInfo>) document.get("patientDetails");
                List<String> conditionCareGapList = document.getList("conditionCareGap", String.class);
                DiagnosListCount diagnosListCount = new DiagnosListCount();
                diagnosListCount.setDiagnosList(diagnosList);
                diagnosListCount.addConditionCareGapCountList(conditionCareGapList);
                diagnosListCount.setPatientDetailsList(patientDetailsList);
                diagnosListCountList.add(diagnosListCount);
            }
            PatientDxAndCareGapModel patientDxAndCareGapModel = new PatientDxAndCareGapModel(diagnosListCountList, populationResult);


            return ResponseEntity.status(StatusCode.OK)
                    .body(new APIResponseEntity<>(APIResponseEntity.Status.SUCCESS, Messages.SUCCESS, patientDxAndCareGapModel));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public ResponseEntity<APIResponseEntity<?>> getPatientFilter(RequestModel model) {

        Query query = new Query();

        if (model.getPatientName() != null && !model.getPatientName().isEmpty()) {
            query.addCriteria(Criteria.where(MedEliteReports.Fields.patientName).is(model.getPatientName()));
        }


        if (model.getPatientCondition() != null && !model.getPatientCondition().isEmpty()) {
            query.addCriteria(Criteria.where(MedEliteReports.Fields.patientConditionDiag1).is(model.getPatientCondition()));
        }
        List<MedEliteReports> medEliteReportsList = mongoTemplate.find(query, MedEliteReports.class);

        List<PatientDetails> patientDetailsList = new ArrayList<>();

        for (MedEliteReports medEliteReports : medEliteReportsList) {
            PatientDetails patientDetailsObj = getPatientDetails(medEliteReports);
            patientDetailsList.add(patientDetailsObj);
        }
        return ResponseEntity.status(StatusCode.OK)
                .body(new APIResponseEntity<>(APIResponseEntity.Status.SUCCESS, Messages.SUCCESS, patientDetailsList));
    }

    @Override
    public ResponseEntity<APIResponseEntity<?>> getPopulationAndConditionCount(RequestModel model) {


        try {
            Query query = new Query();
            ResponseEntity<APIResponseEntity<?>> OK = getResponse(model);
            if (OK != null) return OK;

            query.addCriteria(Criteria.where(FileDataInfo.Fields.month).is(model.getMonth()));

            if (model.getYear() != 0) {
                query.addCriteria(Criteria.where(FileDataInfo.Fields.year).is(model.getYear()));
            }

            if (model.getFacilityName() != null) {
                query.addCriteria(Criteria.where(FileDataInfo.Fields.facilityName).is(model.getFacilityName()));
            }

            if (!model.getPatientCondition().isEmpty() && model.getDx() == 1) {
                query.addCriteria(Criteria.where(FileDataInfo.Fields.patientConditionDiag1).in(model.getPatientCondition()));
            }

            if (!model.getPatientCondition().isEmpty() && model.getDx() == 2) {
                query.addCriteria(Criteria.where(FileDataInfo.Fields.patientConditionDiag2).in(model.getPatientCondition()));
            }

            if (!model.getPatientName().isEmpty()) {
                query.addCriteria(Criteria.where(FileDataInfo.Fields.patientName).in(model.getPatientName()));
            }


            if (model.getAge() != 0) {
                query.addCriteria(Criteria.where(FileDataInfo.Fields.patientAge).gte(model.getAge()));
            }


            List<FileDataInfo> fileDataInfoList = mongoTemplate.find(query, FileDataInfo.class);


            HashMap<String, HashMap<String, Integer>> conditionCareGapCount = new HashMap<>();
            HashMap<String, Integer> populationCountMap = new HashMap<>();
            HashMap<String, List<PatientDetails>> patientWithPopulationMap = new HashMap<>();

            ///
            HashMap<String, Integer> populationCountMap2 = new HashMap<>();
            HashMap<String, List<PatientDetails>> patientWithPopulationMap2 = new HashMap<>();
            HashMap<String, HashMap<String, Integer>> conditionCareGapCount2 = new HashMap<>();

            for (FileDataInfo fileDataInfo : fileDataInfoList) {

                String diagnosList = fileDataInfo.getDiagnosList();
                String diagnosList2 = fileDataInfo.getDiagnosList2();
                String patientDiag = fileDataInfo.getPatientConditionDiag1();
                String patientDiag2 = fileDataInfo.getPatientConditionDiag2();

                conditionCareGapCount.putIfAbsent(diagnosList, new HashMap<>());
                conditionCareGapCount.get(diagnosList).merge(patientDiag, 1, Integer::sum);

                conditionCareGapCount2.putIfAbsent(diagnosList2, new HashMap<>());
                conditionCareGapCount2.get(diagnosList2).merge(patientDiag2, 1, Integer::sum);


                populationCountMap.put(diagnosList, populationCountMap.getOrDefault(diagnosList, 0) + 1);
                populationCountMap2.put(diagnosList2, populationCountMap2.getOrDefault(diagnosList2, 0) + 1);

                PatientDetails patientDetails1 = new PatientDetails();
                patientObject1(fileDataInfo, patientDetails1);

                PatientDetails patientDetails2 = new PatientDetails();
                patientObject2(fileDataInfo, patientDetails2);


                patientWithPopulationMap.putIfAbsent(diagnosList, new ArrayList<>());
                patientWithPopulationMap.get(diagnosList).add(patientDetails1);

                patientWithPopulationMap2.putIfAbsent(diagnosList2, new ArrayList<>());
                patientWithPopulationMap2.get(diagnosList2).add(patientDetails2);

            }

            List<PopulationAndConditionCareCapDetails> populationAndConditionCareCapDetails = new ArrayList<>();
            List<ConditionCareGapCount> conditionCareGaptCountList = new ArrayList<>();

            List<PopulationAndConditionCareCapDetails> populationAndConditionCareCapDetails2 = new ArrayList<>();
            List<ConditionCareGapCount> conditionCareGapCountList2 = new ArrayList<>();

            for (String dx : populationCountMap.keySet()) {
                HashMap<String, Integer> conditionMap = conditionCareGapCount.get(dx);
                Query baseQuery = new Query();
                baseQuery.addCriteria(Criteria.where(ConvertedDiagnosisMappingEntity.Fields.diagnosis).is(dx));

                List<ConvertedDiagnosisMappingEntity> convertedDiagnosisMappingEntities = mongoTemplate.find(baseQuery, ConvertedDiagnosisMappingEntity.class);
                ConvertedDiagnosisMappingEntity convertedDiagnosisMappingEntity = convertedDiagnosisMappingEntities.stream()
                        .filter(i -> i.getDiagnosis().equals(dx))
                        .findFirst()
                        .orElse(null);

                String convertedDx = convertedDiagnosisMappingEntity != null ? convertedDiagnosisMappingEntity.getConvertedDiagnosis() : dx;

                int dxCount = populationCountMap.get(dx);

                ConditionCareGapCount conditionCareGaptCount = new ConditionCareGapCount();
                conditionCareGaptCount.setDiagnosList(dx);
                conditionCareGaptCount.setConvertedDiagnosList(convertedDx);
                conditionCareGaptCount.setDiagnosCount(conditionMap);
                conditionCareGaptCountList.add(conditionCareGaptCount);

                PopulationAndConditionCareCapDetails populationAndConditionCareCapDetails1 = new PopulationAndConditionCareCapDetails();
                populationAndConditionCareCapDetails1.setDiagnosList(dx);
                populationAndConditionCareCapDetails1.setConvertedDiagnosList(convertedDx);

                populationAndConditionCareCapDetails1.setDiagnosListCount(dxCount);
                populationAndConditionCareCapDetails1.setPatientDetailsList(patientWithPopulationMap.get(dx));
                populationAndConditionCareCapDetails.add(populationAndConditionCareCapDetails1);
            }

            for (String dx : patientWithPopulationMap2.keySet()) {
                HashMap<String, Integer> conditionMap = conditionCareGapCount2.get(dx);
                Query baseQuery = new Query();
                baseQuery.addCriteria(Criteria.where(ConvertedDiagnosisMappingEntity.Fields.diagnosis).is(dx));

                List<ConvertedDiagnosisMappingEntity> convertedDiagnosisMappingEntities = mongoTemplate.find(baseQuery, ConvertedDiagnosisMappingEntity.class);
                ConvertedDiagnosisMappingEntity convertedDiagnosisMappingEntity = convertedDiagnosisMappingEntities.stream()
                        .filter(i -> i.getDiagnosis().equals(dx))
                        .findFirst()
                        .orElse(null);

                String convertedDx = convertedDiagnosisMappingEntity != null ? convertedDiagnosisMappingEntity.getConvertedDiagnosis() : dx;

                int dxCount = populationCountMap2.get(dx);

                ConditionCareGapCount conditionCareGaptCount = new ConditionCareGapCount();
                conditionCareGaptCount.setDiagnosList(dx);
                conditionCareGaptCount.setConvertedDiagnosList(convertedDx);
                conditionCareGaptCount.setDiagnosCount(conditionMap);
                conditionCareGapCountList2.add(conditionCareGaptCount);

                PopulationAndConditionCareCapDetails populationAndConditionCareCapDetails1 = new PopulationAndConditionCareCapDetails();
                populationAndConditionCareCapDetails1.setDiagnosList(dx);
                populationAndConditionCareCapDetails1.setConvertedDiagnosList(convertedDx);

                populationAndConditionCareCapDetails1.setDiagnosListCount(dxCount);
                populationAndConditionCareCapDetails1.setPatientDetailsList(patientWithPopulationMap2.get(dx));
                populationAndConditionCareCapDetails2.add(populationAndConditionCareCapDetails1);
            }


            List<String> patientConditionList1 = mongoTemplate.findDistinct(
                    new Query(Criteria.where(FileDataInfo.Fields.facilityName).is(model.getFacilityName())
                            .and(FileDataInfo.Fields.month).is(model.getMonth())),
                    FileDataInfo.Fields.patientConditionDiag1,
                    FileDataInfo.class,
                    String.class
            );

            List<String> patientConditionList2 = mongoTemplate.findDistinct(
                    new Query(Criteria.where(FileDataInfo.Fields.facilityName).is(model.getFacilityName())
                            .and(FileDataInfo.Fields.month).is(model.getMonth())),
                    FileDataInfo.Fields.patientConditionDiag2,
                    FileDataInfo.class,
                    String.class
            );


            populationAndConditionCareCapDetails = populationAndConditionCareCapDetails.stream()
                    .sorted((o1, o2) -> Integer.compare(o2.getDiagnosListCount(), o1.getDiagnosListCount()))
                    .collect(Collectors.toList());


            populationAndConditionCareCapDetails2 = populationAndConditionCareCapDetails2.stream()
                    .sorted((o1, o2) -> Integer.compare(o2.getDiagnosListCount(), o1.getDiagnosListCount()))
                    .collect(Collectors.toList());

            DxAndConditionCareGapResponse dxAndConditionCareGapResponse = new DxAndConditionCareGapResponse(populationAndConditionCareCapDetails, conditionCareGaptCountList, populationAndConditionCareCapDetails2, conditionCareGapCountList2, patientConditionList1, patientConditionList2);


            return ResponseEntity.status(StatusCode.OK)
                    .body(new APIResponseEntity<>(APIResponseEntity.Status.SUCCESS, Messages.SUCCESS, dxAndConditionCareGapResponse));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ResponseEntity<APIResponseEntity<?>> getResponse(RequestModel model) {
        StringBuilder errorMessage = new StringBuilder();

        if (model.getFacilityName() == null || model.getFacilityName().isEmpty()) {
            errorMessage.append("FacilityName is null or empty. ");
        }
        if (model.getMonth() == null || model.getMonth().isEmpty()) {
            errorMessage.append("Month is null or empty. ");
        }
        if (model.getYear() == 0) {
            errorMessage.append("Year is 0. ");
        }
        if (model.getProgram() == null || model.getProgram().isEmpty()) {
            errorMessage.append("ProgramType is null or empty. ");
        }

        if (!errorMessage.isEmpty()) {
            return ResponseEntity.status(StatusCode.NOT_FOUND)
                    .body(new APIResponseEntity<>(
                            APIResponseEntity.Status.FAILED,
                            errorMessage.toString().trim(),
                            "Failed to get data from the DB"
                    ));
        }

        return null;
    }

    private static ResponseEntity<APIResponseEntity<?>> getResponseWithoutMonthCheck(RequestModel model) {
        StringBuilder errorMessage = new StringBuilder();

        if (model.getFacilityName() == null || model.getFacilityName().isEmpty()) {
            errorMessage.append("FacilityName is null or empty. ");
        }
        if (model.getYear() == 0) {
            errorMessage.append("Year is 0. ");
        }
        if (model.getProgramTypes() == null || model.getProgramTypes().isEmpty()) {
            errorMessage.append("ProgramTypes is null or empty. ");
        }

        if (!errorMessage.isEmpty()) {
            return ResponseEntity.status(StatusCode.NOT_FOUND)
                    .body(new APIResponseEntity<>(
                            APIResponseEntity.Status.FAILED,
                            errorMessage.toString().trim(),
                            "Failed to get data from the DB"
                    ));
        }

        return null; // or proceed with further logic
    }

    @Override
    public ResponseEntity<APIResponseEntity<?>> getPatientNames(RequestModel model) {
        try {
            Query query = new Query();
            ResponseEntity<APIResponseEntity<?>> ok = getResponse(model);
            if (ok != null) return ok;
            List<String> patientsNames = mongoTemplate.findDistinct(query, FileDataInfo.Fields.patientName, "fileDataInfo", String.class);
            return ResponseEntity.status(StatusCode.OK)
                    .body(new APIResponseEntity<>(APIResponseEntity.Status.SUCCESS, Messages.SUCCESS, patientsNames));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<APIResponseEntity<?>> getProgramFilter(RequestModel model) {

        try {
            ResponseEntity<APIResponseEntity<?>> ok = getResponseWithoutMonthCheck(model);
            if (ok != null) return ok;

            if( model.getMonths() != null && !model.getMonths().isEmpty() && model.getGetLastMonths() != 0){
                return ResponseEntity.status(StatusCode.BAD_REQUEST)
                        .body(new APIResponseEntity<>(
                                APIResponseEntity.Status.FAILED,
                                "cannot filter the both last "+ model.getGetLastMonths()  + " and " + model.getMonths(),
                                "kindly choose one filter"
                        ));
            }

            DynamicProgramFilter dynamicProgramFilter = new DynamicProgramFilter();
            for (String program : model.getProgramTypes()) {

                Query query = new Query();
                List<String> months = List.of(
                        "JAN", "FEB", "MAR", "APR", "MAY", "JUN",
                        "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"
                );

                setQueryByProgram(model, program, query, months, dynamicProgramFilter);
            }

        if( dynamicProgramFilter.getHtrList() != null && !dynamicProgramFilter.getHtrList().isEmpty()) {
            dynamicProgramFilter.calculateHtrDispositionCount(dynamicProgramFilter.getHtrList());
        }


            return ResponseEntity.status(StatusCode.OK)
                    .body(new APIResponseEntity<>(APIResponseEntity.Status.SUCCESS, Messages.SUCCESS, dynamicProgramFilter));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setQueryByProgram(RequestModel model, String program, Query query, List<String> months, DynamicProgramFilter dynamicProgramFilter) {
            if ("CCM".equals(program)) {
                if(model.getPatientName() != null && !model.getPatientName().isEmpty()){
                    query.addCriteria(Criteria.where(FileDataInfo.Fields.patientName).in(model.getPatientName()));
                }

                query.addCriteria(Criteria.where(FileDataInfo.Fields.programType).is(program));
                query.addCriteria(Criteria.where(FileDataInfo.Fields.year).is(model.getYear()));
                query.fields().include(FileDataInfo.Fields.patientName, FileDataInfo.Fields.hospitalizedFromDate, FileDataInfo.Fields.hospitalizedEndDate, FileDataInfo.Fields.patientGender, FileDataInfo.Fields.physician,
                        FileDataInfo.Fields.daysOfStay, FileDataInfo.Fields.id,FileDataInfo.Fields.year,
                        FileDataInfo.Fields.diagnosList, FileDataInfo.Fields.diagnosList2, FileDataInfo.Fields.residentHospitalized, FileDataInfo.Fields.erVisit,
                        FileDataInfo.Fields.hospitalizedDiagnosis, FileDataInfo.Fields.careGaps, FileDataInfo.Fields.dob,
                        FileDataInfo.Fields.careGaps2, FileDataInfo.Fields.month, FileDataInfo.Fields.physician, FileDataInfo.Fields.patientAge);
                if (model.getGetLastMonths() != 0) {
                    int currentMonthIndex = LocalDate.now().getMonthValue() - 1;
                    List<Integer> lastThreeIndices = getLastNMonthIndices(model.getGetLastMonths(), currentMonthIndex);
                    List<String> lastThreeMonths = lastThreeIndices.stream()
                            .map(months::get)
                            .collect(Collectors.toList());

                    // Add criteria based on the months
                    query.addCriteria(Criteria.where(FileDataInfo.Fields.month).in(lastThreeMonths));
                }

                if(model.getMonths() != null){
                    query.addCriteria(Criteria.where(FileDataInfo.Fields.month).in(model.getMonths()));
                }
                query.addCriteria(Criteria.where(FileDataInfo.Fields.facilityName).is(model.getFacilityName()));
                List<FileDataInfo> fileDataInfoList = mongoTemplate.find(query, FileDataInfo.class);
                dynamicProgramFilter.setCcmList(fileDataInfoList);
            } else if ("HTR".equals(program)) {
                if(model.getPatientName() != null && !model.getPatientName().isEmpty()){
                    query.addCriteria(Criteria.where(HTRInfoEntity.Fields.patientName).in(model.getPatientName()));
                }

                query.addCriteria(Criteria.where(HTRInfoEntity.Fields.programType).is(program));
                query.addCriteria(Criteria.where(HTRInfoEntity.Fields.year).is(model.getYear()));
                if (model.getGetLastMonths() != 0) {
                    int currentMonthIndex = LocalDate.now().getMonthValue() - 1;
                    List<Integer> lastThreeIndices = getLastNMonthIndices(model.getGetLastMonths(), currentMonthIndex);
                    List<String> lastThreeMonths = lastThreeIndices.stream()
                            .map(months::get)
                            .collect(Collectors.toList());
                    // Add criteria based on the months
                    query.addCriteria(Criteria.where(HTRInfoEntity.Fields.month).in(lastThreeMonths));

                }
                if(model.getMonths() != null){
                    query.addCriteria(Criteria.where(HTRInfoEntity.Fields.month).in(model.getMonths()));
                }
                query.addCriteria(Criteria.where(HTRInfoEntity.Fields.facility).is(model.getFacilityName()));

                query.fields().include(HTRInfoEntity.Fields.patientName, HTRInfoEntity.Fields.age, HTRInfoEntity.Fields.dateOfBirth, HTRInfoEntity.Fields.diagnosis, HTRInfoEntity.Fields.month,
                        HTRInfoEntity.Fields.gender, HTRInfoEntity.Fields.transferDate, HTRInfoEntity.Fields.transferByDisposition, HTRInfoEntity.Fields.status
                );
                List<HTRInfoEntity> htrInfoEntityList = mongoTemplate.find(query, HTRInfoEntity.class);
                dynamicProgramFilter.setHtrList(htrInfoEntityList);
            } else if ("BHI".equals(program)) {


                if(model.getPatientName() != null && !model.getPatientName().isEmpty()){
                    query.addCriteria(Criteria.where(BHIInfoEntity.Fields.patientName).in(model.getPatientName()));
                }


                query.addCriteria(Criteria.where(BHIInfoEntity.Fields.programType).is(program));
                query.addCriteria(Criteria.where(BHIInfoEntity.Fields.year).is(model.getYear()));
                if (model.getGetLastMonths() != 0) {
                    int currentMonthIndex = LocalDate.now().getMonthValue() - 1;
                    List<Integer> lastThreeIndices = getLastNMonthIndices(model.getGetLastMonths(), currentMonthIndex);
                    List<String> lastThreeMonths = lastThreeIndices.stream()
                            .map(months::get)
                            .collect(Collectors.toList());

                    // Add criteria based on the months
                    query.addCriteria(Criteria.where(BHIInfoEntity.Fields.month).in(lastThreeMonths));
                }
                if(model.getMonths() != null){
                    query.addCriteria(Criteria.where(BHIInfoEntity.Fields.month).in(model.getMonths()));
                }
                query.addCriteria(Criteria.where(BHIInfoEntity.Fields.facility).is(model.getFacilityName()));

                query.fields().include(BHIInfoEntity.Fields.patientName,BHIInfoEntity.Fields.patientAge,BHIInfoEntity.Fields.shortDx,BHIInfoEntity.Fields.patientGender,
                        BHIInfoEntity.Fields.careGaps,BHIInfoEntity.Fields.dob,BHIInfoEntity.Fields.dos,BHIInfoEntity.Fields.signedBy);
                List<BHIInfoEntity> bhiInfoEntityList = mongoTemplate.find(query, BHIInfoEntity.class);
                dynamicProgramFilter.setBhiList(bhiInfoEntityList);
            }

    }

    @Override
    public ResponseEntity<APIResponseEntity<?>> editProgramFilter(RequestModel model) {

        try {
            if (model.getId() == null || model.getId().isEmpty()) {
                return ResponseEntity.status(StatusCode.BAD_REQUEST)
                        .body(new APIResponseEntity<>(APIResponseEntity.Status.FAILED, "kindly send id to update the field", "failed to update data"));
            }

            Query query = new Query();
            query.addCriteria(Criteria.where(FileDataInfo.Fields.id).is(model.getId()));

            Update update = new Update();

            if(model.getCareGaps1() != null){
                update.set(FileDataInfo.Fields.careGaps,model.getCareGaps1());
            }
            if(model.getCareGaps2() != null){
                update.set(FileDataInfo.Fields.careGaps2,model.getCareGaps1());
            }
            if(model.getResidentHospitalized() != null){
                update.set(FileDataInfo.Fields.residentHospitalized,model.getResidentHospitalized());
            }

            UpdateResult result = mongoTemplate.updateFirst(query,update, FileDataInfo.class);


            return ResponseEntity.status(StatusCode.OK)
                    .body(new APIResponseEntity<>(APIResponseEntity.Status.SUCCESS, "Updated Successfully", result));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<APIResponseEntity<?>> getYears() {
        try {
            List<String> ccmYear = mongoTemplate.findDistinct(FileDataInfo.Fields.year, FileDataInfo.class,Integer.class)
                    .stream()
                    .map(String::valueOf)
                    .toList();


            List<String> htrYear = mongoTemplate.findDistinct(FileDataInfo.Fields.year, HTRInfoEntity.class,Integer.class)
                    .stream()
                    .map(String::valueOf)
                    .toList();


            List<String> bhiYear = mongoTemplate.findDistinct(FileDataInfo.Fields.year, BHIInfoEntity.class,Integer.class)
                    .stream()
                    .map(String::valueOf)
                    .toList();

            Set<String> allYears = new TreeSet<>();
            allYears.addAll(ccmYear);
            allYears.addAll(htrYear);
            allYears.addAll(bhiYear);
            return ResponseEntity.status(StatusCode.OK)
                    .body(new APIResponseEntity<>(APIResponseEntity.Status.SUCCESS, Messages.SUCCESS, allYears));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<APIResponseEntity<?>> cmmDetails(RequestModel model) {
        try {
            Query query = new Query();


            ResponseEntity<APIResponseEntity<?>> ok = getResponse(model);
            if (ok != null) return ok;

            query.addCriteria(Criteria.where(FileDataInfo.Fields.facilityName).is(model.getFacilityName()));
            query.addCriteria(Criteria.where(FileDataInfo.Fields.month).is(model.getMonth()));
            query.addCriteria(Criteria.where(FileDataInfo.Fields.year).is(model.getYear()));
            query.addCriteria(Criteria.where(FileDataInfo.Fields.programType).is(model.getProgram()));

            query.fields().include(FileDataInfo.Fields.patientName, FileDataInfo.Fields.hospitalizedFromDate, FileDataInfo.Fields.hospitalizedEndDate, FileDataInfo.Fields.patientGender, FileDataInfo.Fields.physician,
                    FileDataInfo.Fields.daysOfStay, FileDataInfo.Fields.id, FileDataInfo.Fields.year,
                    FileDataInfo.Fields.diagnosList, FileDataInfo.Fields.diagnosList2, FileDataInfo.Fields.residentHospitalized, FileDataInfo.Fields.erVisit,
                    FileDataInfo.Fields.hospitalizedDiagnosis, FileDataInfo.Fields.careGaps, FileDataInfo.Fields.dob,
                    FileDataInfo.Fields.careGaps2, FileDataInfo.Fields.month, FileDataInfo.Fields.physician, FileDataInfo.Fields.patientAge);


            List<FileDataInfo> fileDataInfoList = mongoTemplate.find(query, FileDataInfo.class);

            return ResponseEntity.status(StatusCode.OK)
                    .body(new APIResponseEntity<>(APIResponseEntity.Status.SUCCESS, Messages.SUCCESS, fileDataInfoList));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private static void patientObject1(FileDataInfo fileDataInfo, PatientDetails patientDetails) {
        patientDetails.setPatientName(fileDataInfo.getPatientName());
        patientDetails.setFacilityName(fileDataInfo.getFacilityName());
        patientDetails.setDiagnosList(fileDataInfo.getDiagnosList());
        patientDetails.setCareGaps(fileDataInfo.getCareGaps());
        patientDetails.setConditionCaregap(fileDataInfo.getPatientConditionDiag1());
    }

    private static void patientObject2(FileDataInfo fileDataInfo, PatientDetails patientDetails) {
        patientDetails.setPatientName(fileDataInfo.getPatientName());
        patientDetails.setFacilityName(fileDataInfo.getFacilityName());
        patientDetails.setDiagnosList(fileDataInfo.getDiagnosList2());
        patientDetails.setCareGaps(fileDataInfo.getCareGaps2());
        patientDetails.setConditionCaregap(fileDataInfo.getPatientConditionDiag2());
    }


    private static PatientDetails getPatientDetails(MedEliteReports medEliteReports) {
        PatientDetails patientDetailsObj = new PatientDetails();
        patientDetailsObj.setCareGaps2(medEliteReports.getCaregaps2());
        patientDetailsObj.setPatientName(medEliteReports.getPatientName());
        patientDetailsObj.setDiagnosList(medEliteReports.getDiagnosList());
        patientDetailsObj.setDiagnosList2(medEliteReports.getDiagnosList_2());
        patientDetailsObj.setFacilityName(medEliteReports.getFacilityName());
        patientDetailsObj.setCareGaps(medEliteReports.getCareGaps());
        patientDetailsObj.setConditionCaregap(medEliteReports.getPatientConditionDiag1());
        patientDetailsObj.setConditionCaregap2(medEliteReports.getPatientConditionDiag2());
        patientDetailsObj.setCareGaps(medEliteReports.getCareGaps());
        patientDetailsObj.setInfluenza(medEliteReports.getInfluenza());
        patientDetailsObj.setCovidBooster(medEliteReports.getCovidBooster());
        patientDetailsObj.setCovidVaccine1(medEliteReports.getCovidVaccine1());
        patientDetailsObj.setCovidVaccine2(medEliteReports.getCovidVaccine2());
        patientDetailsObj.setPneumococcal(medEliteReports.getPneumococcal());
        patientDetailsObj.setPrevnar(medEliteReports.getPrevnar());
        return patientDetailsObj;
    }

    public static String setMonth(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate date = LocalDate.parse(dateString, formatter);
        return date.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH).toUpperCase();
    }

    private List<Integer> getLastNMonthIndices(int n, int currentMonthIndex) {
        // Get the last N indices considering wrap around (modular arithmetic)
        return java.util.stream.IntStream.range(0, n)
                .map(i -> (currentMonthIndex - i + 12) % 12) // Modulo to wrap around if necessary
                .boxed()
                .collect(Collectors.toList());
    }

}


