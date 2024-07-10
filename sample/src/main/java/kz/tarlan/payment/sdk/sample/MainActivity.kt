package kz.tarlan.payment.sdk.sample

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kz.tarlanpayments.storage.androidsdk.TarlanContract
import kz.tarlanpayments.storage.androidsdk.TarlanInput
import kz.tarlanpayments.storage.androidsdk.TarlanOutput

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activityLauncher = registerForActivityResult(TarlanContract()) { result ->

            when (result) {
                is TarlanOutput.Success -> {
                    Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                }

                is TarlanOutput.Failure -> {
                    Toast.makeText(this, "Failure", Toast.LENGTH_SHORT).show()
                }
            }
        }

        setContent {
            var hashField by remember { mutableStateOf("$2a$10\$kl8WLDdGMT0/hKvoGBXU7OvzYBXWze8K9Ql8NhVOkGX32ZBL22EQ2") }
            var transactionId by remember { mutableStateOf("252105244") }

            Column {
                Text(text = "Enter hash")
                TextField(value = hashField, onValueChange = { hashField = it })


                Text(text = "Enter transaction id")
                TextField(value = transactionId, onValueChange = { transactionId = it })


                Button(content = {
                    Text(text = "Open Tarlan SDK")
                }, onClick = {
                    activityLauncher.launch(
                        TarlanInput(
                            hash = hashField,
                            transactionId = transactionId.toLong(),
                            isDebug = false
                        )
                    )
                })
            }
        }
    }
}
