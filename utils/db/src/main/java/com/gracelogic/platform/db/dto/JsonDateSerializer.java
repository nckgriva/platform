package com.gracelogic.platform.db.dto;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

@Component
public class JsonDateSerializer extends JsonSerializer<Date> {
    @Override
    public void serialize(Date date, JsonGenerator gen, SerializerProvider provider) throws IOException, JsonProcessingException {
        if (date != null) {
            String formattedDate = DateFormatConstants.DEFAULT_DATE_FORMAT.get().format(date);
            gen.writeString(formattedDate);
        }
    }
}
