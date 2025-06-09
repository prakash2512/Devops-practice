package com.healthcare.service.implement;

import com.healthcare.entity.FacilityData;
import com.healthcare.entity.HTRInfoEntity;
import com.healthcare.entity.HtrShortAndLongData;
import com.healthcare.model.FilterModel;
import com.healthcare.model.HTRShortAndLongModel;
import com.healthcare.model.RequestModel;
import com.healthcare.model.TransferDetailsModel;
import com.healthcare.service.HTRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class HTRServiceImplements implements HTRService {

    @Autowired
    MongoTemplate mongoTemplate;




    /**
     * method to get distinct status from the database
     * @author sowmiyathangaraj
     */
    @Override
    public List<String> getDistinctStatus(RequestModel model) {

        try {
            if (validateParameters(model))
                throw new Exception("Invalid parameters");
            Query query = new Query();
            query.addCriteria(Criteria.where(HTRInfoEntity.Fields.facility).is(model.getFacilityName()));
            query.addCriteria(Criteria.where(HTRInfoEntity.Fields.month).is(model.getMonth()));
            List<String> statusResponse = mongoTemplate.findDistinct(query, HTRInfoEntity.Fields.status, HTRInfoEntity.class, String.class);
            if (!statusResponse.isEmpty()) {
                return statusResponse;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return List.of();
    }

    private boolean validateParameters(RequestModel model) {
        return model.getFacilityName().isEmpty() || model.getMonth() == null;
    }

    /**
     * method to get distinct provider from the database
     * @author sowmiyathangaraj
     */
    @Override
    public List<String> getDistinctProviders(RequestModel model) {
        try {
            if (validateParameters(model))
                throw new Exception("Invalid parameters");
            Query query = new Query();
            query.addCriteria(Criteria.where(HTRInfoEntity.Fields.facility).is(model.getFacilityName()));
            query.addCriteria(Criteria.where(HTRInfoEntity.Fields.month).is(model.getMonth()));
            List<String> statusResponse = mongoTemplate.findDistinct(query, HTRInfoEntity.Fields.provider, HTRInfoEntity.class, String.class);
            if (!statusResponse.isEmpty()) {
                return statusResponse;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return List.of();
    }




    /**
     * method to get distinct patient from the database
     *
     * @author sowmiyathangaraj
     */
    @Override
    public List<String> getDistinctPatients(RequestModel model) {
        try {
            if (validateParameters(model))
                throw new Exception("Invalid parameters");
            Query query = new Query();
            query.addCriteria(Criteria.where(HTRInfoEntity.Fields.facility).is(model.getFacilityName()));
            query.addCriteria(Criteria.where(HTRInfoEntity.Fields.month).is(model.getMonth()));
            List<String> statusResponse = mongoTemplate.findDistinct(query, HTRInfoEntity.Fields.patientName, HTRInfoEntity.class, String.class);
            if (!statusResponse.isEmpty()) {
                return statusResponse;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return List.of();
    }

    /**
     * method to get HTR details by status
     *
     * @author sowmiyathangaraj
     */
    @Override
    public Map<String, Object> getHTRDetailsByStatus(RequestModel requestModel) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (validateParameters(requestModel))
                throw new Exception("Invalid parameters");

            Criteria defaultCriteria;
            defaultCriteria = Criteria.where(HTRInfoEntity.Fields.facility).is(requestModel.getFacilityName())
                                      .and(HTRInfoEntity.Fields.month).is(requestModel.getMonth());

            // Additional criteria for htrStatus and htrProvider
            if (requestModel.getHtrStatus() != null && !requestModel.getHtrStatus().isEmpty()) {
                defaultCriteria = defaultCriteria.and(HTRInfoEntity.Fields.status).is(requestModel.getHtrStatus());
            }

            if (requestModel.getHtrProvider() != null && !requestModel.getHtrProvider().isEmpty()) {
                defaultCriteria = defaultCriteria.and(HTRInfoEntity.Fields.provider).is(requestModel.getHtrProvider());
            }
            if(requestModel.getPatientName() != null && !requestModel.getPatientName().isEmpty()){
                defaultCriteria = defaultCriteria.and(HTRInfoEntity.Fields.patientName).in(requestModel.getPatientName());
            }
            if(requestModel.getAge() != 0){
                defaultCriteria = defaultCriteria.and(HTRInfoEntity.Fields.age).gte(requestModel.getAge());
            }
            if (requestModel.getCardio() != null) {
                defaultCriteria = defaultCriteria.and(HTRInfoEntity.Fields.cardioProgram).is(requestModel.getCardio());
            }
            if (requestModel.getCardioPulmonary() != null) {
                defaultCriteria = defaultCriteria.and(HTRInfoEntity.Fields.cardioPulmonary).is(requestModel.getCardioPulmonary());
            }

            Criteria finalCriteria = defaultCriteria;


            // Step 2: Aggregations for counts
            result.put("transferByCategory", getAggregatedCounts(HTRInfoEntity.Fields.transferByCategory, finalCriteria));
            result.put("transferByDisposition", getAggregatedCounts(HTRInfoEntity.Fields.transferByDisposition, finalCriteria));
            result.put("transferByStayStatus", getAggregatedCounts(HTRInfoEntity.Fields.stayStatus, finalCriteria));
            result.put("lengthOfStay", getLengthOfStayCounts(finalCriteria));
            result.put("transferByShift", getTransferByShift(finalCriteria));
            result.put("transferByDays", getTransferByDays(finalCriteria));
            result.put("transferByPayerGroup", getAggregatedCounts(HTRInfoEntity.Fields.transferByPayerGroup, finalCriteria));
            result.put("transferByDiagnosis", getAggregatedCounts(HTRInfoEntity.Fields.diagnosis, finalCriteria));
            result.put("categorySubClassification", getAggregatedCounts(HTRInfoEntity.Fields.status, finalCriteria));
            result.put("dispositionDetails", getAggregatedCounts(HTRInfoEntity.Fields.transferByDisposition, finalCriteria));
            result.put("cardio", getAggregatedCounts(HTRInfoEntity.Fields.cardioProgram, finalCriteria));
            result.put("cardioPulmonary", getAggregatedCounts(HTRInfoEntity.Fields.cardioPulmonary, finalCriteria));

            // Step 3: Fetch transfer details
            Query query = new Query(finalCriteria);
            List<TransferDetailsModel> transferDetails = mongoTemplate.find(query, HTRInfoEntity.class).stream()
                    .map(entity -> new TransferDetailsModel(
                            entity.getFirstName() + " " + entity.getLastName(),
                            entity.getGender(),
                            entity.getDiagnosis(),
                            entity.getStayStatus(),
                            formattedDateAndTime(entity.getTransferDate(),entity.getTransferTime())
                    ))
                    .collect(Collectors.toList());

            result.put("transferDetails", transferDetails);
            // Map to dispositionPatientDetails
            List<Map<String, String>> dispositionPatientDetails = mongoTemplate.find(query, HTRInfoEntity.class).stream()
                    .map(entity -> {
                        Map<String, String> patientDetails = new HashMap<>();
                        patientDetails.put("patientName", entity.getFirstName() + " " + entity.getLastName());
                        patientDetails.put("transferDateAndTime", formattedDateAndTime(entity.getTransferDate(),entity.getTransferTime()));
                        patientDetails.put("transferByDisposition",entity.getTransferByDisposition());
                        return patientDetails;
                    })
                    .collect(Collectors.toList());

            result.put("dispositionPatientDetails", dispositionPatientDetails);
            return result;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * method to get transfer by shift
     * @author sowmiyathangaraj
     */
    private Map<String, Integer> getTransferByShift(Criteria finalCriteria) {
        // Initialize the shift count map
        Map<String, Integer> shiftCounts = new HashMap<>();
        shiftCounts.put("morning", 0);
        shiftCounts.put("evening", 0);
        shiftCounts.put("night", 0);

        // Query the database to retrieve transfer times
        Query query = new Query(finalCriteria);
        List<LocalTime> transferTimes = mongoTemplate.find(query, HTRInfoEntity.class)
                .stream()
                .map(HTRInfoEntity::getTransferTime)
                .toList();

        // Categorize each time into a shift and update the counts
        for (LocalTime time : transferTimes) {
            if (time.isBefore(LocalTime.of(12, 0))) { // Before 12:00 PM
                shiftCounts.put("morning", shiftCounts.get("morning") + 1);
            } else if (time.isBefore(LocalTime.of(17, 1))) { // Between 12:00 PM and 5:00 PM
                shiftCounts.put("evening", shiftCounts.get("evening") + 1);
            } else { // After 5:00 PM
                shiftCounts.put("night", shiftCounts.get("night") + 1);
            }
        }

        return shiftCounts.entrySet()
                .stream()
                .filter(entry -> entry.getValue() > 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }



    private String formattedDateAndTime(Date date, LocalTime time) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH.mm");
        String transferDateFormatted = formatter.format(date);
        String transferTimeFormatted = time.format(timeFormatter);
        return transferDateFormatted + ", " + transferTimeFormatted;
    }
    /**
     * method to get aggregated counts from the database
     *
     * @author sowmiyathangaraj
     */
    private Map<String, Integer> getAggregatedCounts(String field, Criteria criteria) {
        Aggregation aggregation = criteria != null
                ? Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group(field).count().as("count")
        )
                : Aggregation.newAggregation(
                Aggregation.group(field).count().as("count")
        );

        return getAggregatedCounts(aggregation);
    }

    /**
     * method to get aggregated counts
     *
     * @author sowmiyathangaraj
     */
    private Map<String, Integer> getAggregatedCounts(Aggregation aggregation) {
        AggregationResults<Map> results = mongoTemplate.aggregate(aggregation, HTRInfoEntity.class, Map.class);
        Map<String, Integer> aggregatedCounts = new HashMap<>();
        results.getMappedResults().forEach(entry -> {
            Object keyObject = entry.get("_id");
            String key = keyObject != null ? keyObject.toString() : null;
            Integer count = (Integer) entry.get("count");
            if (key != null) { // Avoid adding null keys
                aggregatedCounts.put(key, count);
            }
        });
        return aggregatedCounts;
    }


    /**
     * method to get length of stay count from the database
     *
     * @author sowmiyathangaraj
     */

    private Map<String, Integer> getLengthOfStayCounts(Criteria criteria) {
        Aggregation aggregation = criteria != null
                ? Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.project(HTRInfoEntity.Fields.lengthOfStay)
                        .and(ConditionalOperators.when(
                                        Criteria.where(HTRInfoEntity.Fields.lengthOfStay).lte(30)
                                ).then("30 Days or less").otherwise("31 Days or more")
                        ).as("category"),
                Aggregation.group("category").count().as("count")
        )
                : Aggregation.newAggregation(
                Aggregation.project(HTRInfoEntity.Fields.lengthOfStay)
                        .and(ConditionalOperators.when(
                                        Criteria.where(HTRInfoEntity.Fields.lengthOfStay).lte(30)
                                ).then("30 Days or less").otherwise("31 Days or more")
                        ).as("category"),
                Aggregation.group("category").count().as("count")
        );
        return getAggregatedCounts(aggregation);
    }


    /**
     * method to get transfer by days
     * @author sowmiyathangaraj
     */
    private Map<String, Integer> getTransferByDays(Criteria criteria) {
        Aggregation aggregation = criteria != null
                ? Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.project(HTRInfoEntity.Fields.transferDate)
                        .andExpression("dayOfWeek(transferDate)").as("dayOfWeek"),
                Aggregation.group("dayOfWeek").count().as("count")
        )
                : Aggregation.newAggregation(
                Aggregation.project(HTRInfoEntity.Fields.transferDate)
                        .andExpression("dayOfWeek(transferDate)").as("dayOfWeek"),
                Aggregation.group("dayOfWeek").count().as("count")
        );

        AggregationResults<Map> results = mongoTemplate.aggregate(aggregation, HTRInfoEntity.class, Map.class);

        Map<String, Integer> transferByDays = new HashMap<>();
        results.getMappedResults().forEach(entry -> {
            Integer dayOfWeek = (Integer) entry.get("_id");
            //method to get day name by week day
            String dayName = getDayName(dayOfWeek);
            Integer count = (Integer) entry.get("count");
            transferByDays.put(dayName, count);
        });

        return transferByDays;
    }

    /**
     * method to get day name by transfer date
     * @author sowmiyathangraj
     */
    private String getDayName(Integer dayOfWeek) {
        String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        return days[dayOfWeek - 1]; // MongoDB days are 1-based
    }

    /**
     * method to get HTR average census
     * @author sowmiyathangaraj
     */
    @Override
    public List<HTRShortAndLongModel> getHTRAverageCensus(RequestModel requestModel) {
        List<HTRShortAndLongModel> dataList = new ArrayList<>();
        try {
            if(validateParameters(requestModel)) {
                throw new RuntimeException("Invalid parameters");
            }
            Query query = new Query();
            query.addCriteria(Criteria.where(HtrShortAndLongData.Fields.facility).is(requestModel.getFacilityName()));
            query.addCriteria(Criteria.where(HtrShortAndLongData.Fields.month).is(requestModel.getMonth()));
            List<HtrShortAndLongData> response = mongoTemplate.find(query, HtrShortAndLongData.class);
            if(!response.isEmpty()) {
                for(HtrShortAndLongData  data : response) {
                    HTRShortAndLongModel dataModel = new HTRShortAndLongModel();
                    dataModel.setLongAverage(data.getLongAverage());
                    dataModel.setShortAverage(data.getShortAverage());
                    dataModel.setLongNationAverage(data.getLongNationAverage());
                    dataModel.setShortNationAverage(data.getShortNationAverage());
                    dataModel.setLongStateAverage(data.getLongStateAverage());
                    dataModel.setShortStateAverage(data.getShortStateAverage());
                    dataModel.setAdk(data.getAdk());
                    dataList.add(dataModel);
                }
                return dataList;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return dataList;
    }

    /**
     * method to get facility details from the database only valid activeStatus
     * @author sowmiyathangaraj
     */
    @Override
    public List<String> getFacilityDetails() {
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where(FacilityData.Fields.activeStatus).is("1"));
            List<FacilityData> facilityList = mongoTemplate.find(query, FacilityData.class);
            if(!facilityList.isEmpty()) {
                return facilityList.stream().map(FacilityData::getFacilityName).toList();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return List.of();
    }

    @Override
    public FilterModel getFilterDetails(RequestModel requestModel) {
        Query query = new Query();
        query.addCriteria(Criteria.where(HTRInfoEntity.Fields.facility).is(requestModel.getFacilityName()));
        query.addCriteria(Criteria.where(HTRInfoEntity.Fields.month).is(requestModel.getMonth()));
        // Get distinct state values
        List<String> distinctStatuses = mongoTemplate.findDistinct(query, HTRInfoEntity.Fields.status, HTRInfoEntity.class, String.class);

        List<String> distinctPatientNames = mongoTemplate.findDistinct(query,  HTRInfoEntity.Fields.patientName, HTRInfoEntity.class, String.class);

        List<String> distinctProviders = mongoTemplate.findDistinct(query, HTRInfoEntity.Fields.provider, HTRInfoEntity.class, String.class);

        FilterModel filterModel = new FilterModel();
        filterModel.setPatientList(distinctPatientNames);
        filterModel.setProviderList(distinctProviders);
        filterModel.setStatusList(distinctStatuses);
        return filterModel;
    }

    @Override
    public List<HTRInfoEntity>  getPatientDetails(RequestModel model) {

        Query query = new Query();

        if(model.getPatientName() != null){
            query.addCriteria(Criteria.where(HTRInfoEntity.Fields.facility).is(model.getFacilityName()));
        }
        List<HTRInfoEntity> htrInfoEntities = mongoTemplate.find(query, HTRInfoEntity.class);
        return  htrInfoEntities;
    }

}
