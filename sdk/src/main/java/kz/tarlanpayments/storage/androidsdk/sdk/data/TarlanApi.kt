package kz.tarlanpayments.storage.androidsdk.sdk.data

import kz.tarlanpayments.storage.androidsdk.sdk.data.dto.BaseResponse
import kz.tarlanpayments.storage.androidsdk.sdk.data.dto.CardLinkRq
import kz.tarlanpayments.storage.androidsdk.sdk.data.dto.DeleteCardRq
import kz.tarlanpayments.storage.androidsdk.sdk.data.dto.GooglePayRq
import kz.tarlanpayments.storage.androidsdk.sdk.data.dto.InFromSavedRq
import kz.tarlanpayments.storage.androidsdk.sdk.data.dto.InRq
import kz.tarlanpayments.storage.androidsdk.sdk.data.dto.OutFromSavedRq
import kz.tarlanpayments.storage.androidsdk.sdk.data.dto.PayoutRq
import kz.tarlanpayments.storage.androidsdk.sdk.data.dto.ResumeTransactionRq
import kz.tarlanpayments.storage.androidsdk.sdk.data.dto.TransactionBillRs
import kz.tarlanpayments.storage.androidsdk.sdk.data.dto.TransactionColorRs
import kz.tarlanpayments.storage.androidsdk.sdk.data.dto.TransactionInfoMainRs
import kz.tarlanpayments.storage.androidsdk.sdk.data.dto.TransactionInfoPayFormRs
import kz.tarlanpayments.storage.androidsdk.sdk.data.dto.TransactionRs
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

internal interface TarlanApi {

    @GET("view-crafter/api/v1/color/merchants/{merchantId}/projects/{projectId}")
    suspend fun getColors(
        @Path("projectId") projectId: Long,
        @Path("merchantId") merchantId: Long
    ): BaseResponse<TransactionColorRs>

    @GET("view-crafter/api/v1/pay-form/merchants/{merchantId}/projects/{projectId}")
    suspend fun getPayForm(
        @Path("projectId") projectId: Long,
        @Path("merchantId") merchantId: Long
    ): BaseResponse<TransactionInfoPayFormRs>

    @GET("transaction/api/v1/transaction")
    suspend fun getTransaction(
        @Query("transaction_id") transactionId: Long,
        @Query("hash") hash: String
    ): BaseResponse<TransactionInfoMainRs>

    @POST("transaction/api/v1/transaction/pay-in")
    suspend fun payIn(@Body body: InRq): BaseResponse<TransactionRs>

    @POST("transaction/api/v1/transaction/card-link")
    suspend fun cardLink(@Body body: CardLinkRq): BaseResponse<TransactionRs>

    @POST("transaction/api/v1/transaction/pay-out")
    suspend fun payOut(@Body body: PayoutRq): BaseResponse<TransactionRs>

    @POST("transaction/api/v1/transaction/one-click/pay-in")
    suspend fun payInFromSaved(@Body body: InFromSavedRq): BaseResponse<TransactionRs>

    @POST("transaction/api/v1/transaction/one-click/pay-out")
    suspend fun outFromSaved(@Body body: OutFromSavedRq): BaseResponse<TransactionRs>

    @POST("transaction/api/v1/resume")
    suspend fun resumeTransaction(@Body body: ResumeTransactionRq): BaseResponse<TransactionRs>

    @GET("transaction/api/v1/receipt")
    suspend fun getBill(
        @Query("id") transactionId: Long,
        @Query("hash") hash: String
    ): BaseResponse<TransactionBillRs>

    @POST("transaction/api/v1/tokens/deactivate")
    suspend fun deleteCard(@Body body: DeleteCardRq): BaseResponse<Unit>

    @POST("transaction/api/v1/transaction/google-pay")
    suspend fun googlePay(@Body body: GooglePayRq): BaseResponse<TransactionRs>

    @GET("card/api/v1/encryption/public-key")
    suspend fun getPublicKey(): BaseResponse<String>
}

