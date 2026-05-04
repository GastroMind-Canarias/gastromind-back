package com.gastromind.api.infrastructure.adapters.in.rest.dtos.store;

import jakarta.validation.constraints.NotBlank;

public record StoreAliasRequest(
        @NotBlank(message = "Alias obligatorio")
        String alias
) {
}
