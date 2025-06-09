package com.healthcare.service.implement;

import com.healthcare.entity.FileDataInfo;
import com.healthcare.model.ImmunizationCountModel;
import com.healthcare.model.ImmunizationResponse;
import com.healthcare.model.RequestModel;
import com.healthcare.service.ImmunizationService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Service
public class ImmunizationImplements implements ImmunizationService {

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public  List<ImmunizationResponse> getImmunizationDetails(RequestModel model) {

        try{
            List<AggregationOperation> aggregationOperations = new ArrayList<>();
            aggregationOperations.add(Aggregation.match(Criteria.where(FileDataInfo.Fields.year).is(model.getYear())));
            aggregationOperations.add(Aggregation.match(Criteria.where(FileDataInfo.Fields.month).is(model.getMonth())));
            aggregationOperations.add(Aggregation.match(Criteria.where(FileDataInfo.Fields.facilityName).is(model.getFacilityName())));
            aggregationOperations.add(Aggregation.group(FileDataInfo.Fields.influenza,FileDataInfo.Fields.prevnar,FileDataInfo.Fields.pneumococcal,FileDataInfo.Fields.covidVaccine1,FileDataInfo.Fields.covidVaccine2,FileDataInfo.Fields.covidBooster)
                    .count().as("immunizationCount"));

            aggregationOperations.add(Aggregation.project("immunizationCount")
                    .and("_id.influenza").as("influenza")
                    .and("_id.prevnar").as("prevnar")
                    .and("_id.pneumococcal").as("pneumococcal")
                    .and("_id.covidVaccine1").as("covidVaccine1")
                    .and("_id.covidVaccine2").as("covidVaccine2")
                    .and("_id.covidBooster").as("covidBooster")
                    .andExclude("_id")
            );

            Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);

            List<Document> result = mongoTemplate.aggregate(aggregation,"fileDataInfo", Document.class).getMappedResults();


            HashMap<String, HashMap<String,Integer>> immunizationCountMap = new HashMap<>();
            for(Document document : result){
                String[] fields = {"influenza", "prevnar", "pneumococcal", "covidVaccine1", "covidVaccine2", "covidBooster"};
                int immunizationCount = document.getInteger("immunizationCount");

                for (String field : fields) {
                    String value = document.getString(field);
                    immunizationCountMap.putIfAbsent(field, new HashMap<>());
                    // Update the count for the current value of the field
                    immunizationCountMap.get(field).merge(value, immunizationCount, Integer::sum);
                }
            }

            List<ImmunizationResponse> immunizationResponseList = new ArrayList<>();

            for(String immunizationKey:immunizationCountMap.keySet()){
                HashMap<String,Integer> mpp = immunizationCountMap.get(immunizationKey);
                ImmunizationResponse immunizationResponse = new ImmunizationResponse();
                immunizationResponse.setImmunization(immunizationKey);
                List<ImmunizationCountModel> immunizationCountModelList = new ArrayList<>();
                for(String integerHashMapKey : mpp.keySet()){
                    int count = mpp.get(integerHashMapKey);
                    ImmunizationCountModel immunizationCountModel = new ImmunizationCountModel(count,integerHashMapKey);
                    immunizationCountModelList.add(immunizationCountModel);
                }
                immunizationResponse.setImmunizationCountModelList(immunizationCountModelList);
                immunizationResponseList.add(immunizationResponse);
            }

            return immunizationResponseList;


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<FileDataInfo> getResult(RequestModel model) {

        Query query = new Query();
        query.addCriteria(Criteria.where(FileDataInfo.Fields.month).is(model.getMonth()));
        query.addCriteria(Criteria.where(FileDataInfo.Fields.facilityName).is(model.getFacilityName()));
        query.addCriteria(Criteria.where(FileDataInfo.Fields.year).is(model.getYear()));

        List<FileDataInfo>fileDataInfoList = mongoTemplate.find(query,FileDataInfo.class);

        HashMap<String,HashMap<String,Integer>> mpp = new HashMap<>();
        String[] fields = {"influenza", "prevnar", "pneumococcal", "covidVaccine1", "covidVaccine2", "covidBooster"};
        for(FileDataInfo fileDataInfo :fileDataInfoList){
            for(String field : fields){
                mpp.putIfAbsent(field,new HashMap<>());
                String fieldValue = fileDataInfo.getFieldValue(field);
                HashMap<String,Integer> countMap = mpp.get(field);
                countMap.put(fieldValue, countMap.getOrDefault(fieldValue, 0) + 1);
            }
        }

        return List.of();
    }
}
