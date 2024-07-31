package kz.tarlanpayments.storage.androidsdk.sdk

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.androidx.AppNavigator
import kz.tarlanpayments.storage.androidsdk.R
import kz.tarlanpayments.storage.androidsdk.TarlanInput
import kz.tarlanpayments.storage.androidsdk.noui.TarlanInstance

internal class TarlanActivity : AppCompatActivity() {

    companion object {
        private val cicerone = Cicerone.create()
        val router get() = cicerone.router
        val navigatorHolder get() = cicerone.getNavigatorHolder()

        fun intent(context: Context, input: TarlanInput): Intent {
            return Intent(context, TarlanActivity::class.java).apply {
                putExtra("launcher", input)
            }
        }
    }


    private val navigator = AppNavigator(this, R.id.fragment_container)

    private val launcher by lazy {
        intent.getParcelableExtra<TarlanInput>("launcher")!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        TarlanInstance.init(launcher.isDebug)
        val locale = when (launcher.localeCode?.lowercase()) {
            "ru" -> "ru"
            "en" -> "en"
            "kk" -> "kk"
            "kz" -> "kk"
            else -> "ru"
        }
        baseContext.setLocale(locale)
        router.newRootScreen(TarlanScreens.MainScreen(input = launcher, isResume = false))
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
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