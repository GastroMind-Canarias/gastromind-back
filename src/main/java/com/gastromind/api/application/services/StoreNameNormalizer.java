package com.gastromind.api.application.services;

import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class StoreNameNormalizer {
    private static final Set<String> STOP_WORDS = Set.of(
            "supermercado", "supermercados", "s", "sa", "sau", "sl", "slu", "sociedad", "anonima", "group", "grupo");

    public String normalize(String value) {
        if (value == null) {
            return "";
        }
        String clean = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9 ]", " ")
                .replaceAll("\\s+", " ")
                .trim();
        if (clean.isEmpty()) {
            return "";
        }
        return Arrays.stream(clean.split(" "))
                .filter(token -> !token.isBlank())
                .filter(token -> token.length() > 1 || token.matches("\\d+"))
                .filter(token -> !STOP_WORDS.contains(token))
                .distinct()
                .sorted()
                .collect(Collectors.joining(" "));
    }
}
