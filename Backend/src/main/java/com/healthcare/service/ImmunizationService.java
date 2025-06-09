package com.healthcare.service;

import com.healthcare.entity.FileDataInfo;
import com.healthcare.model.ImmunizationResponse;
import com.healthcare.model.RequestModel;
import org.bson.Document;

import java.util.List;

public interface ImmunizationService {
    List<ImmunizationResponse> getImmunizationDetails(RequestModel model);

    List<FileDataInfo> getResult(RequestModel model);
}
