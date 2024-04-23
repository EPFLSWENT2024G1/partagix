package com.android.partagix.model

import android.content.Intent
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.lifecycle.ViewModel
import com.android.partagix.model.stampDimension.StampDimension
import com.android.partagix.ui.MainActivity
import qrcode.QRCode
import qrcode.QRCodeBuilder

public const val CREATE_PNG_FILE = 50
public const val WRITE_PNG_FILE = 51

class StampViewModel(context: MainActivity) : ViewModel() {
  private val context = context
  private val qrCodeBuilder = QRCode.ofRoundedSquares()

  init {}

  private fun setSize(
      qrCodeBuilder: QRCodeBuilder,
      itemId: String,
      label: String,
      dim: StampDimension
  ) {
    when (dim) {
      StampDimension.SMALL -> qrCodeBuilder.withSize(5)
      StampDimension.MEDIUM -> qrCodeBuilder.withSize(10)
      StampDimension.BIG -> qrCodeBuilder.withSize(15)
      StampDimension.FULL_PAGE -> qrCodeBuilder.withSize(20)
    }
  }

  private fun addLabel(pngBytes: ByteArray, label: String) {}

  fun generateQRCodeAndSave(itemId: String, label: String, detailedDimension: String) {
    var dim = getStampDimension(detailedDimension)
    setSize(qrCodeBuilder, itemId, label, dim)
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

  companion object {}

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
