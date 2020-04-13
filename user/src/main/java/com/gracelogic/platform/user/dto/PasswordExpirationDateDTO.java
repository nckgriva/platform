package com.gracelogic.platform.user.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gracelogic.platform.db.dto.JsonDateDeserializer;
import com.gracelogic.platform.db.dto.JsonDateSerializer;

import java.util.Date;

public class PasswordExpirationDateDTO {
    private Date date;

    @JsonSerialize(using = JsonDateSerializer.class, include=JsonSerialize.Inclusion.ALWAYS)
    @JsonDeserialize(using = JsonDateDeserializer.class)
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public PasswordExpirationDateDTO(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "PasswordExpirationDateDTO{" +
                "date=" + date +
                '}';
    }
}
