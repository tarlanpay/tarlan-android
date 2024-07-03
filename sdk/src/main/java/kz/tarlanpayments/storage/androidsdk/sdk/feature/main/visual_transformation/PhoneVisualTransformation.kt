package kz.tarlanpayments.storage.androidsdk.sdk.feature.main.visual_transformation

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

internal class CardNumberVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 16) text.text.substring(0..15) else text.text
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i % 4 == 3 && i != 15) out += " "
        }
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return offset + (offset / 4).coerceAtMost(3)
            }

            override fun transformedToOriginal(offset: Int): Int {
                return (offset - (offset / 5)).coerceAtLeast(0)
            }
        }
        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}

