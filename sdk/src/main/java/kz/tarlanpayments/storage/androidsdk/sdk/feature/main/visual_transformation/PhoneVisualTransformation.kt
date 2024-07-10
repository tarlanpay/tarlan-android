package kz.tarlanpayments.storage.androidsdk.sdk.feature.main.visual_transformation

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
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

internal fun generalMaskPhoneTransformation(
    text: AnnotatedString,
    mask: String = "+7(***) *** ** **",
    maskPlaceholder: Char = '*'
): TransformedText {
    val cleanNumber = mask.filter { it == maskPlaceholder }
    val trimmed =
        (if (text.text.trim().length > cleanNumber.length) text.text.trim()
            .substring(0, cleanNumber.length) else text.text).trim().filter { it.isDigit() }
    val annotatedString = buildAnnotatedString {
        if (trimmed.isEmpty()) {
            append(mask[0])
            append(mask[1])
        }

        var maskIndex = 0
        var textIndex = 0
        while (textIndex < trimmed.length && maskIndex < mask.length) {
            if (mask[maskIndex] != maskPlaceholder) {
                val nextDigitIndex = mask.indexOf(maskPlaceholder, maskIndex)
                append(mask.substring(maskIndex, nextDigitIndex))
                maskIndex = nextDigitIndex
            }
            append(trimmed[textIndex++])
            maskIndex++
        }
        toAnnotatedString()
    }

    val phoneNumberOffsetTranslator = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            var noneDigitCount = 2
            var i = 2
            while (i < offset + noneDigitCount) {
                if (mask[i++] != maskPlaceholder) noneDigitCount++
            }
            return offset + noneDigitCount
        }

        override fun transformedToOriginal(offset: Int): Int {
            return offset - mask.take(offset).count { it != maskPlaceholder }
        }
    }

    return TransformedText(annotatedString, phoneNumberOffsetTranslator)
}