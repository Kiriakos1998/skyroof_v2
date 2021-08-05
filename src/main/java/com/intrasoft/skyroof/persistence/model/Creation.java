package com.intrasoft.skyroof.persistence.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.intrasoft.skyroof.utils.LocalDateTimeDeserializer;
import com.intrasoft.skyroof.utils.LocalDateTimeSerializer;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import java.io.Serializable;
import java.time.LocalDateTime;

@MappedSuperclass
public abstract class Creation implements Serializable {

    @PrePersist
    public void prePersist(){
        if(creationDate == null){
            creationDate = LocalDateTime.now();
        }
    }

    @Column(name = "creation_date", nullable = false)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime creationDate;

    public LocalDateTime getCreationDate() {
        return creationDate;
    }
}
