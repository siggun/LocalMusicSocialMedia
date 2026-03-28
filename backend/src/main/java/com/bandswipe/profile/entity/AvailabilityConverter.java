package com.bandswipe.profile.entity;

import com.bandswipe.shared.enums.Availability;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Converter
public class AvailabilityConverter implements AttributeConverter<Set<Availability>, String> {

    @Override
    public String convertToDatabaseColumn(Set<Availability> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }
        return attribute.stream()
                .map(Enum::name)
                .sorted()
                .collect(Collectors.joining(","));
    }

    @Override
    public Set<Availability> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return Collections.emptySet();
        }
        return Arrays.stream(dbData.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Availability::valueOf)
                .collect(Collectors.toSet());
    }
}
