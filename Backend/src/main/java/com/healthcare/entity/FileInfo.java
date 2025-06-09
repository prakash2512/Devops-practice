package com.healthcare.entity;

import com.healthcare.domain.Audit;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Document()
public class FileInfo extends Audit {

    @Id
    private String id;
    private String fileName;
    private LocalDate month;
    private Instant uploadTime;
}
