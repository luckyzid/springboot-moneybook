package kr.money.book.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonUtil {

    private static final ObjectMapper mapper = JsonMapper.builder()
        .addModules(new JavaTimeModule())
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true)
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        .defaultTimeZone(TimeZone.getDefault())
        .defaultLocale(Locale.getDefault())
        .build();

    private JsonUtil() {
    }

    public static Optional<String> toJson(Object object) {
        try {
            return Optional.of(mapper.writeValueAsString(object));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> toMap(String source) {
        try {
            return mapper.readValue(source, Map.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Collections.emptyMap();
        }
    }

    public static <T> Optional<T> toClass(String source, Class<T> clazz) {
        try {
            return Optional.of(mapper.readValue(source, clazz));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    public static <T> Optional<T> toType(String source, TypeReference<T> typeReference) {
        try {
            return Optional.of(mapper.readValue(source, typeReference));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    public static <T> Optional<T> objectToType(Object object, TypeReference<T> typeReference) {
        try {
            return Optional.of(mapper.convertValue(object, typeReference));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    public static <E> List<E> toCollection(String source, Class<E> clazz) {
        try {
            final CollectionType javaType = mapper.getTypeFactory()
                .constructCollectionType(List.class, clazz);
            return mapper.readValue(source, javaType);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}
