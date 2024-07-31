package kz.tarlanpayments.storage.androidsdk.noui

import kz.tarlanpayments.storage.androidsdk.noui.data.TarlanApi
import kz.tarlanpayments.storage.androidsdk.noui.data.TarlanCookieManager
import kz.tarlanpayments.storage.androidsdk.noui.data.TarlanHeaderInterceptor
import kz.tarlanpayments.storage.androidsdk.noui.data.TarlanRepositoryImpl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object TarlanInstance {

    fun init(isDebug: Boolean) {
        this.isDebug = isDebug
    }

    val tarlanRepository: TarlanRepository by lazy { TarlanRepositoryImpl() }

    var isDebug = false
    private var mrcdnUrl = "https://mrcdn.tarlanpayments.kz/"
    private var mrapiUrl = "https://mrapi.tarlanpayments.kz/"
    private var prapiUrl = "https://prapi.tarlanpayments.kz/"

    private var debugMrcdnUrl = "https://mrcdn-sandbox.tarlanpayments.kz/"
    private var debugMrapiUrl = "https://sandboxmrapi.tarlanpayments.kz/"
    private var deubgPrapiUrl = "https://sandboxapi.tarlanpayments.kz/"

    internal val mrapi by lazy {
        Retrofit.Builder()
            .baseUrl(if (isDebug) debugMrapiUrl else mrapiUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(TarlanApi::class.java)
    }

    internal val retrofit: TarlanApi by lazy {
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