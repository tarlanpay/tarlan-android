package kz.tarlanpayments.storage.androidsdk.sdk.ui.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

internal fun createPdfFromBitmap(context: Context, bitmap: Bitmap, fileName: String) {
    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
    val page = pdfDocument.startPage(pageInfo)

    val canvas = page.canvas
    canvas.drawBitmap(bitmap, 0f, 0f, null)
    pdfDocument.finishPage(page)

    // Сохраняем PDF в файловую систему
    try {
        val fileOutputStream = FileOutputStream("${context.filesDir}/$fileName")
        pdfDocument.writeTo(fileOutputStream)
        fileOutputStream.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }

    pdfDocument.close()
}

internal fun sharePdf(context: Context, fileName: String) {
    val file = File(context.filesDir, fileName)
    val contentUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)

    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, contentUri)
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    }

    context.startActivity(Intent.createChooser(shareIntent, "Share PDF"))
}