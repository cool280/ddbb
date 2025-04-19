package com.ddbb.mongo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.*;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.core.convert.converter.Converter;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class MongoConfig {

    @Bean(name = "mongoTemplate")
    public MongoTemplate mongoTemplate(MongoDatabaseFactory factory, MongoMappingContext context) {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
        MappingMongoConverter mappingConverter = new MappingMongoConverter(dbRefResolver, context);
        //去掉_class字段
        mappingConverter.setTypeMapper(new DefaultMongoTypeMapper(null));
        //mappingConverter.setCustomConversions(customConversions());
        return new MongoTemplate(factory,mappingConverter);
    }

    @Bean
    public MongoCustomConversions customConversions() {
        List<Converter<?,?>> converters = new ArrayList<Converter<?,?>>();
        converters.add(new DateToLongConverter());
        converters.add(new LongToDateConverter());
        converters.add(new TimestampConverter());
        return new MongoCustomConversions(converters);
    }
}

