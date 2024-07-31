package kz.tarlanpayments.storage.androidsdk.noui.data.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

internal data class BaseResponse<T>(
    @SerializedName("status") @Expose val status: Boolean,
    @SerializedName("message") @Expose val message: String,
    @SerializedName("result") @Expose val result: T,
    @SerializedName("status_code") @Expose val statusCode: Int? = null,
)