package ru.neoflex.dev.spring.life_without_spring_data;

import java.util.HashMap;
import java.util.Map;

/**
 * A {@code Map<String, Object>} builder. This builder IS NOT reusable. Entities created inside this builder
 * ARE SHALLOW copied with each other. DO NOT REUSE THIS BUILDER!
 */
public class SqlParametersMapBuilder {

    private Map<String, Object> params = new HashMap<>();

    public static SqlParametersMapBuilder builder() {
        return new SqlParametersMapBuilder();
    }

    public SqlParametersMapBuilder with(String paramName, Object paramValue) {
        this.params.put(paramName, paramValue);
        return this;
    }

    public SqlParametersMapBuilder withIfNotNull(String paramName, Object paramValue) {
        if (paramValue != null) {
            this.params.put(paramName, paramValue);
        }
        return this;
    }

    public Map build() {
        var newHashMap = new HashMap<>();
        return params;
    }
}
