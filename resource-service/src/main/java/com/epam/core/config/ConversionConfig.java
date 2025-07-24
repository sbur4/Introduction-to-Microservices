package com.epam.core.config;

import com.epam.core.converter.GetSongEntityToResponseDtoConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Configuration
public class ConversionConfig {

    @Bean
    @Primary
    public ConversionServiceFactoryBean initConversionService() {
        log.info("Starting initialization of primary ConversionService bean.");

        ConversionServiceFactoryBean factoryBean = new ConversionServiceFactoryBean();
        factoryBean.setConverters(getConverters());

        log.info("Primary ConversionService bean initialized successfully with '{}' custom converters.", getConverters().size());
        return factoryBean;
    }

    private static Set<Converter<?, ?>> getConverters() {
        return Stream.of(
                new GetSongEntityToResponseDtoConverter()
        ).collect(Collectors.toSet());
    }

    @Bean
    @ConditionalOnMissingBean
    public ConversionService defaultConversionService() {
        return new DefaultConversionService();
    }
}
