package com.android.partagix.model

import android.annotation.SuppressLint
import android.content.Intent
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.lifecycle.ViewModel
import com.android.partagix.MainActivity
import com.android.partagix.model.stampDimension.StampDimension
import qrcode.QRCode
import qrcode.QRCodeBuilder

const val CREATE_PNG_FILE = 50

class StampViewModel(@SuppressLint("StaticFieldLeak") private val context: MainActivity) :
    ViewModel() {
  private val qrCodeBuilder = QRCode.ofRoundedSquares()

  private fun setSize(qrCodeBuilder: QRCodeBuilder, dim: StampDimension) {
    when (dim) {
      StampDimension.SMALL -> qrCodeBuilder.withSize(5)
      StampDimension.MEDIUM -> qrCodeBuilder.withSize(10)
      StampDimension.BIG -> qrCodeBuilder.withSize(15)
      StampDimension.FULL_PAGE -> qrCodeBuilder.withSize(20)
    }
  }
  // TODO : add the label to the qr code
  // private fun addLabel(pngBytes: ByteArray, label: String) {}

  fun generateQRCodeAndSave(itemId: String, label: String, detailedDimension: String) {
    val dim = getStampDimension(detailedDimension)
    setSize(qrCodeBuilder, dim)
    val qrCode = qrCodeBuilder.build(itemId).renderToBytes()
    val intent =
        Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
          addCategory(Intent.CATEGORY_OPENABLE)
          type = "image/png"
        }
    intent.putExtra(Intent.EXTRA_TITLE, "qr-code.png")
    context.setQrBytes(qrCode)
    startActivityForResult(context, intent, CREATE_PNG_FILE, null)
  }

  /**
   * Get the StampDimension given the detailedDimension string.
   *
   * @param detailedDimension the detailed dimension string
   * @return the StampDimension corresponding to the detailedDimension, or StampDimension.MEDIUM if
   *   not found
   */
  private fun getStampDimension(detailedDimension: String): StampDimension {
    var ret = StampDimension.MEDIUM
    for (stampDimension in StampDimension.values()) {
      if (stampDimension.detailedDimension == detailedDimension) {
        ret = stampDimension
      }
    }
    return ret
  }
}
