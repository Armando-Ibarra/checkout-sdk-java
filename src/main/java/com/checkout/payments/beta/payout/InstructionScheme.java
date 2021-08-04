package com.checkout.payments.beta.payout;

import com.google.gson.annotations.SerializedName;

public enum InstructionScheme {

    @SerializedName("swift")
    SWIFT,
    @SerializedName("local")
    LOCAL,
    @SerializedName("instant")
    INSTANT

}
