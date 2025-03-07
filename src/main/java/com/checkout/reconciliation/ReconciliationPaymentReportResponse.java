package com.checkout.reconciliation;

import com.checkout.common.Resource;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class ReconciliationPaymentReportResponse extends Resource {

    private int count;

    private List<PaymentReportData> data;
}
