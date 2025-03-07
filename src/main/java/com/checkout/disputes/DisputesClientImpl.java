package com.checkout.disputes;

import com.checkout.AbstractClient;
import com.checkout.ApiClient;
import com.checkout.CheckoutConfiguration;
import com.checkout.SecretKeyCredentials;
import com.checkout.common.FileDetailsResponse;
import com.checkout.common.FileRequest;
import com.checkout.common.IdResponse;

import java.util.concurrent.CompletableFuture;

public class DisputesClientImpl extends AbstractClient implements DisputesClient {

    private static final String DISPUTES = "/disputes";
    private static final String FILES = "/files";
    private static final String ACCEPT = "accept";
    private static final String EVIDENCE = "evidence";

    public DisputesClientImpl(final ApiClient apiClient, final CheckoutConfiguration configuration) {
        super(apiClient, SecretKeyCredentials.fromConfiguration(configuration));
    }

    @Override
    public CompletableFuture<DisputesQueryResponse> query(final DisputesQueryFilter queryFilter) {
        return apiClient.queryAsync(DISPUTES, apiCredentials, queryFilter, DisputesQueryResponse.class);
    }

    @Override
    public CompletableFuture<DisputeDetailsResponse> getDisputeDetails(final String id) {
        return apiClient.getAsync(constructApiPath(DISPUTES, id), apiCredentials, DisputeDetailsResponse.class);
    }

    @Override
    public CompletableFuture<Void> accept(final String id) {
        return apiClient.postAsync(constructApiPath(DISPUTES, id, ACCEPT), apiCredentials, Void.class, null, null);
    }


    @Override
    public CompletableFuture<Void> putEvidence(final String id, final DisputeEvidenceRequest disputeEvidence) {
        return apiClient.putAsync(constructApiPath(DISPUTES, id, EVIDENCE), apiCredentials, Void.class, disputeEvidence);
    }

    @Override
    public CompletableFuture<DisputeEvidenceResponse> getEvidence(final String id) {
        return apiClient.getAsync(constructApiPath(DISPUTES, id, EVIDENCE), apiCredentials, DisputeEvidenceResponse.class);
    }

    @Override
    public CompletableFuture<Void> submitEvidence(final String id) {
        return apiClient.postAsync(constructApiPath(DISPUTES, id, EVIDENCE), apiCredentials, Void.class, null, null);
    }

    @Override
    public CompletableFuture<IdResponse> uploadFile(final FileRequest request) {
        return apiClient.submitFileAsync(FILES, apiCredentials, request, IdResponse.class);
    }

    @Override
    public CompletableFuture<FileDetailsResponse> getFileDetails(final String id) {
        return apiClient.getAsync(constructApiPath(FILES, id), apiCredentials, FileDetailsResponse.class);
    }

}
