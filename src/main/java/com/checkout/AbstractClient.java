package com.checkout;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.checkout.common.CheckoutUtils.requiresNonNull;

public abstract class AbstractClient {

    protected final ApiClient apiClient;
    protected final ApiCredentials apiCredentials;

    protected AbstractClient(final ApiClient apiClient, final ApiCredentials apiCredentials) {
        requiresNonNull("apiClient", apiClient);
        requiresNonNull("apiCredentials", apiCredentials);
        this.apiClient = apiClient;
        this.apiCredentials = apiCredentials;
    }

    protected static String constructApiPath(final String... pathParams) {
        return Stream.of(pathParams)
                .collect(Collectors.joining("/"));
    }

}