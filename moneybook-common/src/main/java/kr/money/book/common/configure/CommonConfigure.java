package kr.money.book.common.configure;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.Locale;
import java.util.TimeZone;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.convention.NameTokenizers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfigure {

    @Bean
    public ModelMapper modelMapper() {

        return configureModelMapper(new ModelMapper());
    }

    @Bean
    public ModelMapper camelToUnderScoreCaseModelMapper() {

        ModelMapper mapper = new ModelMapper();
        configureModelMapper(mapper);
        mapper.getConfiguration()
            .setSourceNameTokenizer(NameTokenizers.CAMEL_CASE)
            .setDestinationNameTokenizer(NameTokenizers.UNDERSCORE);

        return mapper;
    }

    @Bean
    public ModelMapper underScoreToCamelCaseModelMapper() {

        ModelMapper mapper = new ModelMapper();
        configureModelMapper(mapper);
        mapper.getConfiguration()
            .setSourceNameTokenizer(NameTokenizers.UNDERSCORE)
            .setDestinationNameTokenizer(NameTokenizers.CAMEL_CASE);

        return mapper;
    }

    private ModelMapper configureModelMapper(ModelMapper mapper) {
        mapper.getConfiguration()
            .setSkipNullEnabled(true)
            .setFieldMatchingEnabled(true)
            .setFieldAccessLevel(AccessLevel.PRIVATE)
            .setMatchingStrategy(MatchingStrategies.STRICT);

        return mapper;
    }

    @Bean
    public ObjectMapper objectMapper() {

        return new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .setTimeZone(TimeZone.getDefault())
            .setLocale(Locale.getDefault());
    }
}
