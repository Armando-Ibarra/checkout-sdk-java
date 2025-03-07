package com.checkout.customers;

import com.checkout.common.Phone;
import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public final class CustomerDetailsResponse {

    private String id;
    private String email;
    @SerializedName("default")
    private String defaultId;
    private String name;
    private Phone phone;
    private Map<String, Object> metadata;
    private List<String> instruments;
}
