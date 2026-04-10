package com.gastromind.api.infrastructure.adapters.out.ai;

import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

/**
 * Decides whether a failed Gemini HTTP call should be retried with the next model in the chain
 * (overload, rate limits, transient server/network errors).
 */
public final class GeminiRetryPolicy {

    private GeminiRetryPolicy() {
    }

    /**
     * @return true if the same request might succeed on another model (or transiently on retry).
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
