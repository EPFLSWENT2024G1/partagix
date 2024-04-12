package com.android.partagix.model

import android.R.attr.text
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Align
import android.graphics.Typeface
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.lifecycle.ViewModel
import com.android.partagix.model.stampDimension.StampDimension
import qrcode.QRCode
import qrcode.QRCodeBuilder
import java.io.File
import java.io.FileOutputStream


class StampViewModel(itemId: String, label : String, dim : StampDimension) : ViewModel() {

    init {
        val squares = QRCode.ofRoundedSquares()
        setSize(squares, itemId, label, dim)
        val qrCode = squares.build(itemId).renderToBytes()
        val intent = createFile()
        startActivityForResult(FileSaveActivity(), intent, CREATE_PNG_FILE, null)
    }

    private fun setSize(squares: QRCodeBuilder, itemId : String, label: String, dim: StampDimension) {
        when(dim) {
            StampDimension.SMALL -> squares.withSize(5)
            StampDimension.MEDIUM -> squares.withSize(10)
            StampDimension.BIG -> squares.withSize(15)
            StampDimension.FULL_PAGE -> squares.withSize(20)
        }
    }
    private fun addLabel(pngBytes: ByteArray, label: String) {

    }
    private fun createFile(filename : String): Intent {
        val outputStream: FileOutputStream
        try {
            val file = File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName)
            outputStream = FileOutputStream(file)
            qrCodeBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }



        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
            putExtra(Intent.EXTRA_TITLE, "qr-code.pdf")

            // Optionally, specify a URI for the directory that should be opened in
            // the system file picker before your app creates the document.
            //putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }
        return intent
    }
    companion object {
        private const val CREATE_PNG_FILE = 50
    }
}

