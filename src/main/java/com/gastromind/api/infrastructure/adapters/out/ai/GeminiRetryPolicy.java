package com.gastromind.api.infrastructure.adapters.out.ai;

import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

public final class GeminiRetryPolicy {

    private GeminiRetryPolicy() {
    }
    /**
     * Realiza should try next model.
     * @param failure valor a utilizar.
     * @return true si cumple la condicion; false en caso contrario.
     */

    public static boolean shouldTryNextModel(Throwable failure) {
        Throwable cur = failure;
        while (cur != null) {
            if (cur instanceof RestClientResponseException rce) {
                int code = rce.getStatusCode().value();
                return code == 408 || code == 429 || code == 500 || code == 502 || code == 503 || code == 504;
            }
            if (cur instanceof ResourceAccessException) {
                return true;
            }
            cur = cur.getCause();
        }
        return false;
    }
}




