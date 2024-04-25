package com.android.partagix.model

import android.annotation.SuppressLint
import android.content.Intent
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.lifecycle.ViewModel
import com.android.partagix.model.stampDimension.StampDimension
import com.android.partagix.ui.MainActivity
import qrcode.QRCode
import qrcode.QRCodeBuilder

public const val CREATE_PNG_FILE = 50

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
    var dim = StampDimension.MEDIUM
    getDetailedDimensionOrdinal(detailedDimension) { d -> dim = d }
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
   * @param onSuccess the function to call when the dimension is found
   */
  private fun getDetailedDimensionOrdinal(
      detailedDimension: String,
      onSuccess: (StampDimension) -> Unit
  ) {
    for (stampDimension in StampDimension.values()) {
      if (stampDimension.detailedDimension == detailedDimension) {
        onSuccess(stampDimension)
      }
    }
  }
}
