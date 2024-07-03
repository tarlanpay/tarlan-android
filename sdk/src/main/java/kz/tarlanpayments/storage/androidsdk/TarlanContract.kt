package kz.tarlanpayments.storage.androidsdk

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import kz.tarlanpayments.storage.androidsdk.sdk.TarlanActivity

class TarlanContract : ActivityResultContract<TarlanInput, TarlanOutput>() {
    override fun createIntent(context: Context, input: TarlanInput): Intent {
        return TarlanActivity.intent(context, input)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): TarlanOutput {
        return when (resultCode) {
            Activity.RESULT_OK -> {
                val transactionId = intent?.getLongExtra("transactionId", 0L)
                if (transactionId != null) {
                    TarlanOutput.Success(transactionId)
                } else {
                    TarlanOutput.Failure("Transaction ID is missing")
                }
            }

            else -> {
                val error = intent?.getStringExtra("error")
                if (error != null) {
                    TarlanOutput.Failure(error)
                } else {
                    TarlanOutput.Failure("Unknown error")
                }
            }
        }
    }
}