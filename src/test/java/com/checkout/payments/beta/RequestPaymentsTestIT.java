package com.checkout.payments.beta;

import com.checkout.common.beta.Address;
import com.checkout.common.beta.Currency;
import com.checkout.common.beta.Phone;
import com.checkout.payments.beta.request.PaymentRequest;
import com.checkout.payments.beta.request.Payments;
import com.checkout.payments.beta.request.ThreeDSRequest;
import com.checkout.payments.beta.request.source.PaymentSourceType;
import com.checkout.payments.beta.request.source.RequestCardSource;
import com.checkout.payments.beta.request.source.RequestIdSource;
import com.checkout.payments.beta.request.source.RequestTokenSource;
import com.checkout.payments.beta.response.PaymentResponse;
import com.checkout.payments.beta.response.PaymentResponseBalances;
import com.checkout.payments.beta.response.PaymentStatus;
import com.checkout.payments.beta.response.ThreeDSEnrollmentData;
import com.checkout.payments.beta.response.ThreeDSEnrollmentStatus;
import com.checkout.payments.beta.response.source.ResponseCardSource;
import com.checkout.payments.beta.sender.RequestCorporateSender;
import com.checkout.payments.beta.sender.RequestIndividualSender;
import com.checkout.tokens.beta.CardTokenResponse;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.checkout.payments.beta.CardSourceHelper.getCorporateSender;
import static com.checkout.payments.beta.CardSourceHelper.getIndividualSender;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RequestPaymentsTestIT extends AbstractPaymentsTestIT {

    @Test
    public void shouldMakeCardPayment() {

        final RequestCardSource source = RequestCardSource.builder()
                .number(CardSourceHelper.Visa.NUMBER)
                .expiryMonth(CardSourceHelper.Visa.EXPIRY_MONTH)
                .expiryYear(CardSourceHelper.Visa.EXPIRY_YEAR)
                .ccv(CardSourceHelper.Visa.CCV)
                .stored(false)
                .build();

        final RequestIndividualSender sender = RequestIndividualSender.builder()
                .firstName("John")
                .lastName("Doe")
                .address(Address.builder()
                        .addressLine1("Address Line 1")
                        .addressLine2("Address Line 2")
                        .city("City")
                        .country("UK")
                        .build())
                .build();

        final PaymentRequest request = Payments.card(source).individualSender(sender)
                .capture(false)
                .reference(UUID.randomUUID().toString())
                .amount(10L)
                .currency(Currency.EUR)
                .build();

        final PaymentResponse<ResponseCardSource> paymentResponse = blocking(getApiV2().paymentsClient().requestPayment(request));

        assertNotNull(paymentResponse);
        assertNotNull(paymentResponse.getId());
        assertEquals(Long.valueOf(10), paymentResponse.getAmount());
        assertEquals(Currency.EUR, paymentResponse.getCurrency());
        assertTrue(paymentResponse.isApproved());
        assertEquals(PaymentStatus.AUTHORIZED, paymentResponse.getStatus());
        assertEquals("Approved", paymentResponse.getResponseSummary());
        assertNull(paymentResponse.getThreeDSEnrollment());
        assertNull(paymentResponse.getCustomer());
        assertNotNull(paymentResponse.getReference());
        assertNotNull(paymentResponse.getProcessing());
        assertEquals(4, paymentResponse.getLinks().size());
        assertEquals(PaymentResponseBalances.builder()
                .availableToCapture(10L)
                .availableToRefund(0L)
                .availableToVoid(10L)
                .totalAuthorized(10L)
                .totalCaptured(0L)
                .totalRefunded(0L)
                .totalVoided(0L)
                .build(), paymentResponse.getBalances()
        );
        final ResponseCardSource responseCardSource = paymentResponse.getSource();
        assertNotNull(responseCardSource);
        assertEquals(PaymentSourceType.CARD, responseCardSource.getType());
        assertEquals(CardSourceHelper.Visa.EXPIRY_MONTH, (int) responseCardSource.getExpiryMonth());
        assertEquals(CardSourceHelper.Visa.EXPIRY_YEAR, (int) responseCardSource.getExpiryYear());
        assertEquals("Visa", responseCardSource.getScheme());

    }

    @Test
    public void shouldMakeCardPayment_3ds() {

        final RequestCardSource source = RequestCardSource.builder()
                .number(CardSourceHelper.Visa.NUMBER)
                .expiryMonth(CardSourceHelper.Visa.EXPIRY_MONTH)
                .expiryYear(CardSourceHelper.Visa.EXPIRY_YEAR)
                .ccv(CardSourceHelper.Visa.CCV)
                .stored(false)
                .build();

        final RequestIndividualSender sender = RequestIndividualSender.builder()
                .firstName("John")
                .lastName("Doe")
                .address(Address.builder()
                        .addressLine1("Address Line 1")
                        .addressLine2("Address Line 2")
                        .city("City")
                        .country("UK")
                        .build())
                .build();

        final ThreeDSRequest threeDSRequest = ThreeDSRequest.builder().enabled(true).build();

        final PaymentRequest request = Payments.card(source).individualSender(sender)
                .capture(false)
                .reference(UUID.randomUUID().toString())
                .amount(45L)
                .currency(Currency.EUR)
                .threeDSRequest(threeDSRequest)
                .successUrl("https://test.checkout.com/success")
                .failureUrl("https://test.checkout.com/failure")
                .build();

        final PaymentResponse<ResponseCardSource> paymentResponse = blocking(getApiV2().paymentsClient().requestPayment(request));

        assertNotNull(paymentResponse);
        assertNotNull(paymentResponse.getId());
        assertNull(paymentResponse.getAmount());
        assertNull(paymentResponse.getCurrency());
        assertFalse(paymentResponse.isApproved());
        assertEquals(PaymentStatus.PENDING, paymentResponse.getStatus());
        assertNull(paymentResponse.getResponseSummary());
        assertEquals(ThreeDSEnrollmentData.builder().enrolled(ThreeDSEnrollmentStatus.YES).build(), paymentResponse.getThreeDSEnrollment());
        assertNull(paymentResponse.getCustomer());
        assertNotNull(paymentResponse.getReference());
        assertNull(paymentResponse.getProcessing());
        assertEquals(3, paymentResponse.getLinks().size());
        assertNull(paymentResponse.getBalances());
        final ResponseCardSource responseCardSource = paymentResponse.getSource();
        assertNull(responseCardSource);

    }

    @Test
    public void shouldMakeCardVerification() {

        final RequestCardSource source = RequestCardSource.builder()
                .number(CardSourceHelper.Visa.NUMBER)
                .expiryMonth(CardSourceHelper.Visa.EXPIRY_MONTH)
                .expiryYear(CardSourceHelper.Visa.EXPIRY_YEAR)
                .ccv(CardSourceHelper.Visa.CCV)
                .stored(false)
                .build();

        final RequestIndividualSender sender = RequestIndividualSender.builder()
                .firstName("John")
                .lastName("Doe")
                .address(Address.builder()
                        .addressLine1("Address Line 1")
                        .addressLine2("Address Line 2")
                        .city("City")
                        .country("UK")
                        .build())
                .build();

        final PaymentRequest request = Payments.card(source).individualSender(sender)
                .capture(false)
                .reference(UUID.randomUUID().toString())
                .amount(0L)
                .currency(Currency.EUR)
                .build();

        final PaymentResponse<ResponseCardSource> paymentResponse = blocking(getApiV2().paymentsClient().requestPayment(request));

        assertNotNull(paymentResponse);
        assertNotNull(paymentResponse.getId());
        assertEquals(Long.valueOf(0), paymentResponse.getAmount());
        assertEquals(Currency.EUR, paymentResponse.getCurrency());
        assertTrue(paymentResponse.isApproved());
        assertEquals(PaymentStatus.CARD_VERIFIED, paymentResponse.getStatus());
        assertEquals("Approved", paymentResponse.getResponseSummary());
        assertNull(paymentResponse.getThreeDSEnrollment());
        assertNull(paymentResponse.getCustomer());
        assertNotNull(paymentResponse.getReference());
        assertNotNull(paymentResponse.getProcessing());
        assertEquals(2, paymentResponse.getLinks().size());
        assertEquals(PaymentResponseBalances.builder()
                .availableToCapture(0L)
                .availableToRefund(0L)
                .availableToVoid(0L)
                .totalAuthorized(0L)
                .totalCaptured(0L)
                .totalRefunded(0L)
                .totalVoided(0L)
                .build(), paymentResponse.getBalances()
        );
        final ResponseCardSource responseCardSource = paymentResponse.getSource();
        assertNotNull(responseCardSource);
        assertEquals(PaymentSourceType.CARD, responseCardSource.getType());
        assertEquals(CardSourceHelper.Visa.EXPIRY_MONTH, (int) responseCardSource.getExpiryMonth());
        assertEquals(CardSourceHelper.Visa.EXPIRY_YEAR, (int) responseCardSource.getExpiryYear());
        assertEquals("Visa", responseCardSource.getScheme());

    }

    @Test
    public void shouldMakeIdSourcePayment() {

        final PaymentResponse<ResponseCardSource> cardPaymentResponse = makeCardPayment(false);

        // id payment
        final RequestIdSource idSource = RequestIdSource.builder()
                .id(cardPaymentResponse.getSource().getId())
                .ccv(CardSourceHelper.Visa.CCV)
                .build();

        final RequestIndividualSender sender = getIndividualSender();

        final PaymentRequest idSourceRequest = Payments.id(idSource).individualSender(sender)
                .capture(false)
                .reference(UUID.randomUUID().toString())
                .amount(16L)
                .currency(Currency.EUR)
                .build();

        final PaymentResponse<ResponseCardSource> paymentResponse = blocking(getApiV2().paymentsClient().requestPayment(idSourceRequest));

        assertNotNull(paymentResponse);
        assertNotNull(paymentResponse.getId());
        assertEquals(Long.valueOf(16), paymentResponse.getAmount());
        assertEquals(Currency.EUR, paymentResponse.getCurrency());
        assertTrue(paymentResponse.isApproved());
        assertEquals(PaymentStatus.AUTHORIZED, paymentResponse.getStatus());
        assertEquals("Approved", paymentResponse.getResponseSummary());
        assertNull(paymentResponse.getThreeDSEnrollment());
        assertNull(paymentResponse.getCustomer());
        assertNotNull(paymentResponse.getReference());
        assertNotNull(paymentResponse.getProcessing());
        assertEquals(4, paymentResponse.getLinks().size());
        assertEquals(PaymentResponseBalances.builder()
                .availableToCapture(16L)
                .availableToRefund(0L)
                .availableToVoid(16L)
                .totalAuthorized(16L)
                .totalCaptured(0L)
                .totalRefunded(0L)
                .totalVoided(0L)
                .build(), paymentResponse.getBalances()
        );
        final ResponseCardSource responseCardSource = paymentResponse.getSource();
        assertNotNull(responseCardSource);
        assertEquals(PaymentSourceType.CARD, responseCardSource.getType());
        assertEquals(CardSourceHelper.Visa.EXPIRY_MONTH, (int) responseCardSource.getExpiryMonth());
        assertEquals(CardSourceHelper.Visa.EXPIRY_YEAR, (int) responseCardSource.getExpiryYear());
        assertEquals("Visa", responseCardSource.getScheme());

    }

    @Test
    public void shouldMakeTokenPayment() {

        final CardTokenResponse cardTokenResponse = getToken();

        final RequestTokenSource tokenSource = RequestTokenSource.builder()
                .token(cardTokenResponse.getToken())
                .billingAddress(Address.builder()
                        .addressLine1("Address Line 1")
                        .addressLine2("Address Line 2")
                        .city("City")
                        .country("ES")
                        .build())
                .phone(Phone.builder().number("675676541").countryCode("+34").build())
                .build();

        final RequestCorporateSender sender = getCorporateSender();

        final PaymentRequest tokenRequest = Payments.token(tokenSource).corporateSender(sender)
                .capture(false)
                .reference(UUID.randomUUID().toString())
                .amount(3456L)
                .currency(Currency.USD)
                .build();

        final PaymentResponse<ResponseCardSource> paymentResponse = blocking(getApiV2().paymentsClient().requestPayment(tokenRequest));

        assertNotNull(paymentResponse);
        assertNotNull(paymentResponse.getId());
        assertEquals(Long.valueOf(3456), paymentResponse.getAmount());
        assertEquals(Currency.USD, paymentResponse.getCurrency());
        assertTrue(paymentResponse.isApproved());
        assertEquals(PaymentStatus.AUTHORIZED, paymentResponse.getStatus());
        assertEquals("Approved", paymentResponse.getResponseSummary());
        assertNull(paymentResponse.getThreeDSEnrollment());
        assertNull(paymentResponse.getCustomer());
        assertNotNull(paymentResponse.getReference());
        assertNotNull(paymentResponse.getProcessing());
        assertEquals(4, paymentResponse.getLinks().size());
        assertEquals(PaymentResponseBalances.builder()
                .availableToCapture(3456L)
                .availableToRefund(0L)
                .availableToVoid(3456L)
                .totalAuthorized(3456L)
                .totalCaptured(0L)
                .totalRefunded(0L)
                .totalVoided(0L)
                .build(), paymentResponse.getBalances()
        );
        final ResponseCardSource responseCardSource = paymentResponse.getSource();
        assertNotNull(responseCardSource);
        assertEquals(PaymentSourceType.CARD, responseCardSource.getType());

    }

    @Test
    public void shouldMakeTokenPayment_3ds() {

        final CardTokenResponse cardTokenResponse = getToken();

        final RequestTokenSource tokenSource = RequestTokenSource.builder()
                .token(cardTokenResponse.getToken())
                .billingAddress(Address.builder()
                        .addressLine1("Address Line 1")
                        .addressLine2("Address Line 2")
                        .city("City")
                        .country("ES")
                        .build())
                .phone(Phone.builder().number("675676541").countryCode("+34").build())
                .build();

        final RequestIndividualSender sender = getIndividualSender();

        final ThreeDSRequest threeDSRequest = ThreeDSRequest.builder().enabled(true).build();

        final PaymentRequest tokenRequest = Payments.token(tokenSource).individualSender(sender)
                .capture(false)
                .reference(UUID.randomUUID().toString())
                .amount(677777L)
                .currency(Currency.USD)
                .threeDSRequest(threeDSRequest)
                .successUrl("https://test.checkout.com/success")
                .failureUrl("https://test.checkout.com/failure")
                .build();

        final PaymentResponse<ResponseCardSource> paymentResponse = blocking(getApiV2().paymentsClient().requestPayment(tokenRequest));

        assertNotNull(paymentResponse);
        assertNotNull(paymentResponse.getId());
        assertNull(paymentResponse.getAmount());
        assertNull(paymentResponse.getCurrency());
        assertFalse(paymentResponse.isApproved());
        assertEquals(PaymentStatus.PENDING, paymentResponse.getStatus());
        assertNull(paymentResponse.getResponseSummary());
        assertEquals(ThreeDSEnrollmentData.builder().enrolled(ThreeDSEnrollmentStatus.YES).build(), paymentResponse.getThreeDSEnrollment());
        assertNull(paymentResponse.getCustomer());
        assertNotNull(paymentResponse.getReference());
        assertNull(paymentResponse.getProcessing());
        assertEquals(3, paymentResponse.getLinks().size());
        assertNull(paymentResponse.getBalances());
        final ResponseCardSource responseCardSource = paymentResponse.getSource();
        assertNull(responseCardSource);

    }

}
