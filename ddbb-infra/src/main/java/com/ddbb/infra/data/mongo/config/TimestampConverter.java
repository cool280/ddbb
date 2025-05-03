package com.ddbb.infra.data.mongo.config;
import org.springframework.core.convert.converter.Converter;
import java.util.Date;
import java.sql.Timestamp;

public class TimestampConverter implements Converter<Date, Timestamp>{
    @Override
    public Timestamp convert(Date date) {
        if(date != null){
            return new Timestamp(date.getTime());
        }
        return null;
    }
}
