package com.spotprice.api.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Map;

public record ErrorDetail(
        String code,
        String message,
        @JsonIgnore Map<String, Object> extra
) {
    public ErrorDetail(String code, String message) {
        this(code, message, Map.of());
    }

    @JsonAnyGetter
    public Map<String, Object> getExtra() {
        return extra;
    }
}
