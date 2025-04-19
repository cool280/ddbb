package com.ddbb.mongo.config;

import java.util.Date;
import org.springframework.core.convert.converter.Converter;


public class LongToDateConverter implements Converter<Long, Date> {
    @Override
    public Date convert(Long source) {
        if(source == null){
            return null;
        }
        return new Date(source);
    }
}
