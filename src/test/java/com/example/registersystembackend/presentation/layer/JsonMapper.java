package com.example.registersystembackend.presentation.layer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.ResultActions;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public final class JsonMapper {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonMapper() {
    }

    public static <T> String objectToString(T object) throws JsonProcessingException {
        return MAPPER.writeValueAsString(object);
    }

    public static <T> T resultToObject(ResultActions result, TypeReference<T> typeReference) throws JsonProcessingException, UnsupportedEncodingException {
        return MAPPER.readValue(result.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8), typeReference);
    }
}
