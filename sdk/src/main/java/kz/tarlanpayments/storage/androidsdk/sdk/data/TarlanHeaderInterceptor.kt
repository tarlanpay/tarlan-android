package kz.tarlanpayments.storage.androidsdk.sdk.data

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

internal class TarlanHeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        csrfToken?.let {
            if (it.isNotEmpty()) {
                Log.d("TarlanHeaderInterceptor", "Set for ${requestBuilder.build().url}: $it")
                requestBuilder.addHeader("X-Csrf", it)
            }
        }
        val response = chain.proceed(requestBuilder.build())
        response.header("X-Csrf")?.let {
            if (it.isNotEmpty()) {
                csrfToken = it
            }
        }
        return response
    }

    companion object {
        private var csrfToken: String? = null
    }
}