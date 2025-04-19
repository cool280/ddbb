package com.ddbb.mongo.config;

import java.util.Date;
import org.springframework.core.convert.converter.Converter;

public class DateToLongConverter implements Converter<Date, Long> {
    @Override
    public Long convert(Date source) {
        if(source == null){
            return null;
        }
        return source.getTime();
    }
}
