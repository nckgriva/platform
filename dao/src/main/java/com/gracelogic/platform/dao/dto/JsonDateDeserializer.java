package com.gracelogic.platform.dao.dto;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Author: Igor Parkhomenko
 * Date: 04.01.2015
 * Time: 12:23
 */
@Component
public class JsonDateDeserializer extends JsonDeserializer<Date> {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        String date = jsonParser.getText();
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }
}
