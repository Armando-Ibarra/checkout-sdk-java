package com.checkout.payments.links;

import com.checkout.ApiClient;
import com.checkout.ApiCredentials;
import com.checkout.CheckoutConfiguration;
import com.checkout.TestHelper;
import com.checkout.payments.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentLinksClientImplTest {

    private static final String REFERENCE = "ORD-1234";

    @Mock
    private ApiClient apiClient;

    @Mock
    private CheckoutConfiguration configuration;

    @Mock
    private PaymentLinkResponse paymentLinkResponse;

    @Mock
    private PaymentLinkDetailsResponse paymentLinkDetailsResponse;

    @Mock
    private CompletableFuture<PaymentLinkResponse> paymentLinkAsync;

    @Mock
    private CompletableFuture<PaymentLinkDetailsResponse> paymentLinkDetailAsync;

    private PaymentLinksClient client;

    private PaymentLinkRequest paymentLinksRequest;

    @BeforeEach
    public void setUp() {
        paymentLinkAsync = CompletableFuture.completedFuture(paymentLinkResponse);
        paymentLinkDetailAsync = CompletableFuture.completedFuture(paymentLinkDetailsResponse);
        client = new PaymentLinksClientImpl(apiClient, configuration);
        paymentLinksRequest = TestHelper.createPaymentLinksRequest(REFERENCE);
    }

    private void setUpPaymentLinkResponse() {
        when(paymentLinkResponse.getReference()).thenReturn(paymentLinksRequest.getReference());
        when(paymentLinkResponse.getLinks()).thenReturn(new HashMap<>());
    }

    private void setUpPaymentLinkDetailsResponse() {
        when(paymentLinkDetailsResponse.getReference()).thenReturn(REFERENCE);
        when(paymentLinkDetailsResponse.getStatus()).thenReturn(PaymentStatus.ACTIVE);
        when(paymentLinkDetailsResponse.getReturnUrl()).thenReturn(paymentLinksRequest.getReturnUrl());
        when(paymentLinkDetailsResponse.getAmount()).thenReturn(paymentLinksRequest.getAmount());
        when(paymentLinkDetailsResponse.getCurrency()).thenReturn(paymentLinksRequest.getCurrency());
        when(paymentLinkDetailsResponse.getDescription()).thenReturn(paymentLinksRequest.getDescription());
        when(paymentLinkDetailsResponse.getCustomer()).thenReturn(paymentLinksRequest.getCustomer());
        when(paymentLinkDetailsResponse.getBilling()).thenReturn(paymentLinksRequest.getBilling());
        when(paymentLinkDetailsResponse.getProducts()).thenReturn(paymentLinksRequest.getProducts());
        when(paymentLinkDetailsResponse.getAmount()).thenReturn(paymentLinksRequest.getAmount());
        when(paymentLinkDetailsResponse.getAmount()).thenReturn(paymentLinksRequest.getAmount());
    }

    @Test
    public void shouldCreatePaymentsLink() throws ExecutionException, InterruptedException {
        setUpPaymentLinkDetailsResponse();
        Mockito.doReturn(paymentLinkDetailAsync)
                .when(apiClient).getAsync(eq(PaymentLinksClientImpl.PAYMENT_LINKS + "/" + REFERENCE),
                any(ApiCredentials.class),
                eq(PaymentLinkDetailsResponse.class));
        final PaymentLinkDetailsResponse response = client.getAsync(REFERENCE).get();
        assertNotNull(response);
        assertEquals(REFERENCE, response.getReference());
        assertThat(response.getStatus(), anyOf(equalTo(PaymentStatus.ACTIVE), equalTo(PaymentStatus.PAYMENT_RECEIVED), equalTo(PaymentStatus.EXPIRED)));
        assertEquals(paymentLinksRequest.getReturnUrl(), response.getReturnUrl());
        assertEquals(paymentLinksRequest.getAmount(), response.getAmount());
        assertEquals(paymentLinksRequest.getCurrency(), response.getCurrency());
        assertEquals(paymentLinksRequest.getReference(), response.getReference());
        assertEquals(paymentLinksRequest.getDescription(), response.getDescription());
        assertEquals(paymentLinksRequest.getCustomer(), response.getCustomer());
        assertEquals(paymentLinksRequest.getBilling(), response.getBilling());
        assertEquals(paymentLinksRequest.getProducts(), response.getProducts());
    }

    @Test
    public void shouldRetrievePaymentsLink() throws ExecutionException, InterruptedException {
        setUpPaymentLinkResponse();
        Mockito.doReturn(paymentLinkAsync)
                .when(apiClient).postAsync(eq(PaymentLinksClientImpl.PAYMENT_LINKS), any(ApiCredentials.class),
                eq(PaymentLinkResponse.class), any(PaymentLinkRequest.class), any());
        final PaymentLinkResponse response = client.createAsync(paymentLinksRequest).get();
        assertNotNull(response);
        assertEquals(paymentLinksRequest.getReference(), response.getReference());
        assertNotNull(response.getLinks());
    }

}