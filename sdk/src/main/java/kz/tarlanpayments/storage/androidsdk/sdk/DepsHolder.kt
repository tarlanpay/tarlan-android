package kz.tarlanpayments.storage.androidsdk.sdk

import android.content.Context
import kz.tarlanpayments.storage.androidsdk.sdk.data.TarlanApi
import kz.tarlanpayments.storage.androidsdk.sdk.data.TarlanCookieManager
import kz.tarlanpayments.storage.androidsdk.sdk.data.TarlanHeaderInterceptor
import kz.tarlanpayments.storage.androidsdk.sdk.data.TarlanRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal object DepsHolder {
    var isDebug = false
    val tarlanRepository by lazy { TarlanRepository() }
    private var mrcdnUrl = "https://mrcdn.tarlanpayments.kz/"
    private var mrapiUrl = "https://mrapi.tarlanpayments.kz/"
    private var prapiUrl = "https://prapi.tarlanpayments.kz/"

    private var debugMrcdnUrl = "https://mrcdn-sandbox.tarlanpayments.kz/"
    private var debugMrapiUrl = "https://sandboxmrapi.tarlanpayments.kz/"
    private var deubgPrapiUrl = "https://sandboxapi.tarlanpayments.kz/"

    val mrapi by lazy {
        Retrofit.Builder()
            .baseUrl(if (isDebug) debugMrapiUrl else mrapiUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(TarlanApi::class.java)
    }
    val retrofit: TarlanApi by lazy {
        Retrofit.Builder()
            .baseUrl(if (isDebug) deubgPrapiUrl else prapiUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(TarlanApi::class.java)
    }


    fun provideImage(id: String): String {
        return (if (isDebug) debugMrcdnUrl else mrcdnUrl) + id
    }

    private val okHttpClient by lazy {
        OkHttpClient
            .Builder()
            .cookieJar(TarlanCookieManager())
            .addInterceptor(TarlanHeaderInterceptor())
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .callTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .build()
    }
}

internal fun Context.setLocale(locale: String) {
    val sharedPreferences =
        this.getSharedPreferences("TarlanPaymentSdk", Context.MODE_PRIVATE)
    sharedPreferences.edit().putString("currentLocale", locale).apply()
}

internal fun Context.provideCurrentLocale(): String {
    return this.getSavedLocale()
}

private fun Context.getSavedLocale(): String {
    val sharedPreferences =
        this.getSharedPreferences("TarlanPaymentSdk", Context.MODE_PRIVATE)
    return sharedPreferences.getString("currentLocale", this.getCurrentSystemLocale())!!
}

private fun Context.getCurrentSystemLocale(): String {
    val locale = this.resources.configuration.locale.country.uppercase()

    return when (locale) {
        "EN" -> return "EN"
        "KK" -> return "KK"
        "KZ" -> return "KZ"
        "RU" -> return "RU"
        else -> return "RU"
    }
}