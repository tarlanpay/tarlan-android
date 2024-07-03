package kz.tarlanpayments.storage.androidsdk.sdk

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.androidx.AppNavigator
import kz.tarlanpayments.storage.androidsdk.R
import kz.tarlanpayments.storage.androidsdk.TarlanInput

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
        DepsHolder.isDebug = launcher.isDebug
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