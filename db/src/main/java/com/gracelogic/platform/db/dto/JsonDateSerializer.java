package com.gracelogic.platform.db.dto;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Author: Igor Parkhomenko
 * Date: 04.01.2015
 * Time: 12:23
 */
@Component
public class JsonDateSerializer extends JsonSerializer<Date> {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

    @Override
    public void serialize(Date date, JsonGenerator gen, SerializerProvider provider) throws IOException, JsonProcessingException {
        if (date != null) {
            String formattedDate = dateFormat.format(date);
            gen.writeString(formattedDate);
        }
    }
}
