package kz.tarlanpayments.storage.androidsdk.noui.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

class Tarlan3DSV2Contract : ActivityResultContract<Tarlan3DSV2Input, Boolean>() {
    override fun createIntent(context: Context, input: Tarlan3DSV2Input): Intent {
        return Tarlan3DSV2Activity.intent(context, input)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
        return resultCode == Activity.RESULT_OK
    }
}