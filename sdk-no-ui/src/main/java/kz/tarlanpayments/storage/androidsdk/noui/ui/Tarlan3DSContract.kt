package kz.tarlanpayments.storage.androidsdk.noui.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

class Tarlan3DSContract : ActivityResultContract<Tarlan3DSInput, Boolean>() {
    override fun createIntent(context: Context, input: Tarlan3DSInput): Intent {
        return Tarlan3DSActivity.intent(context, input)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
        return resultCode == Activity.RESULT_OK
    }
}