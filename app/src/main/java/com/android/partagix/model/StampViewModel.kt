package com.android.partagix.model

import android.app.Activity
import android.content.Intent
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.lifecycle.ViewModel
import com.android.partagix.model.stampDimension.StampDimension
import qrcode.QRCode
import qrcode.QRCodeBuilder


class StampViewModel(context : Activity) : ViewModel() {
    private val qrCodeBuilder = QRCode.ofRoundedSquares()
    private val context = context
    init {

    }

    private fun setSize(qrCodeBuilder: QRCodeBuilder, itemId : String, label: String, dim: StampDimension) {
        when(dim) {
            StampDimension.SMALL -> qrCodeBuilder.withSize(5)
            StampDimension.MEDIUM -> qrCodeBuilder.withSize(10)
            StampDimension.BIG -> qrCodeBuilder.withSize(15)
            StampDimension.FULL_PAGE -> qrCodeBuilder.withSize(20)
        }
    }
    private fun addLabel(pngBytes: ByteArray, label: String) {

    }

    fun generateQRCodeAndSave(itemId: String, label: String, dim: StampDimension) {
        setSize(qrCodeBuilder, itemId, label, dim)
        val qrCode = qrCodeBuilder.build(itemId).renderToBytes()
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/png"

        }
        //intent.putExtra(Intent.EXTRA_STREAM, qrCode).putExtra(Intent.EXTRA_TITLE, "qr-code.png") TODO : add qr code to intent
        startActivityForResult(context, intent, CREATE_PNG_FILE, null)
    }


  /**
   * Get the ordinal of the dimension, given the detailedDimension string.
   *
   * @param detailedDimension the detailed dimension string
   * @return the ordinal of the dimension, or -1 if the dimension is not found
   */
  fun getDetailedDimensionOrdinal(detailedDimension: String): Int {
    for (stampDimension in StampDimension.values()) {
      if (stampDimension.detailedDimension == detailedDimension) {
        return stampDimension.ordinal
      }
    }
    return -1
  }

    companion object {
        private const val CREATE_PNG_FILE = 50
    }
}

