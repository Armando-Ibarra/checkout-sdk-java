package com.checkout.payments;

import com.checkout.PlatformType;
import com.checkout.SandboxTestFixture;
import com.checkout.TestHelper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CardDestinationPaymentTestIT extends SandboxTestFixture {

    public CardDestinationPaymentTestIT() {
        super(PlatformType.CLASSIC);
    }

    @Test
    public void canPerformCardPayout() throws Exception {
        final PaymentRequest<CardSource> paymentRequest = TestHelper.createCardPayoutRequest();
        final PaymentResponse paymentResponse = getApi().paymentsClient().requestAsync(paymentRequest).get();
        assertNull(paymentResponse.getPayment());
        assertNotNull(paymentResponse.getPending());
        assertNotNull(paymentResponse.getPending().getId());
        assertEquals("Pending", paymentResponse.getPending().getStatus());
        assertNotNull(paymentResponse.getPending().getReference());
        assertNotNull(paymentResponse.getPending().getCustomer());
        assertNull(paymentResponse.getPending().getThreeDS());
        assertNotNull(paymentResponse.getPending().getSelfLink());
    }

}
