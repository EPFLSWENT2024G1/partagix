package com.android.partagix.model

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.lifecycle.ViewModel
import com.android.partagix.MainActivity
import com.android.partagix.model.stampDimension.StampDimension
import java.io.ByteArrayOutputStream
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

  private fun addLabel(pngBytes: ByteArray, label: String, dim: StampDimension): ByteArray {
    // Convert ByteArray to Bitmap
    val originalBitmap = BitmapFactory.decodeByteArray(pngBytes, 0, pngBytes.size)
    val width = originalBitmap.width

    val addedSpace =
        when (dim) {
          StampDimension.SMALL -> 40
          StampDimension.MEDIUM -> 80
          StampDimension.BIG -> 120
          StampDimension.FULL_PAGE -> 200
        }
    val height = originalBitmap.height + addedSpace

    val mutableBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    // Create a Canvas object with the Bitmap
    val canvas = Canvas(mutableBitmap)

    // Paint the new bottom part white
    canvas.drawColor(Color.WHITE)

    // Create a Paint object for drawing text
    val paint = Paint()
    paint.color = Color.BLACK // Text color
    paint.textSize =
        when (dim) {
          StampDimension.SMALL -> 20f
          StampDimension.MEDIUM -> 40f
          StampDimension.BIG -> 60f
          StampDimension.FULL_PAGE -> 80f
        }
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD) // Text style

    // Calculate text position (you can adjust this according to your requirement)
    val textWidth = paint.measureText(label)
    val x = (width - textWidth) / 2
    val y = originalBitmap.height + addedSpace * 0.6f

    // Draw the original image onto the Canvas
    canvas.drawBitmap(originalBitmap, 0f, 0f, null)

    // Draw text onto the Canvas
    canvas.drawText(label, x, y, paint)

    // Convert Bitmap back to ByteArray
    val outputStream = ByteArrayOutputStream()
    mutableBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    return outputStream.toByteArray()
  }

  fun generateQRCodeAndSave(itemId: String, label: String, detailedDimension: String) {
    val dim = getStampDimension(detailedDimension)
    setSize(qrCodeBuilder, dim)
    val qrCode = qrCodeBuilder.build(itemId).renderToBytes()
    val qrCodeWithLabel =
        if (label != "") {
          addLabel(qrCode, label, dim)
        } else {
          qrCode
        }

    val intent =
        Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
          addCategory(Intent.CATEGORY_OPENABLE)
          type = "image/png"
        }
    intent.putExtra(Intent.EXTRA_TITLE, "qr-code.png")
    context.setQrBytes(qrCodeWithLabel)
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
