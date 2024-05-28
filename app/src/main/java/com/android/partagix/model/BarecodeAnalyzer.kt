import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.android.partagix.model.auth.Authentication
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class BarcodeAnalyzer(
    private val context: Context,
    private val onQrScanned: (String, String) -> Unit
) : ImageAnalysis.Analyzer {

  private val options =
      BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).build()

  private val scanner = BarcodeScanning.getClient(options)

  @OptIn(ExperimentalGetImage::class)
  override fun analyze(imageProxy: ImageProxy) {
    imageProxy.image?.let { image ->
      scanner
          .process(InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees))
          .addOnSuccessListener { barcode -> onSuccess(barcode) }
          .addOnCompleteListener { imageProxy.close() }
    }
  }

  fun onSuccess(barcode: List<Barcode>) {
    barcode
        ?.takeIf { it.isNotEmpty() }
        ?.mapNotNull { it.rawValue }
        ?.joinToString(",")
        ?.let {
          val user = Authentication.getUser()
          if (user != null) {
            val uri = Uri.parse(it)
            val itemId = uri.getQueryParameter("itemId")
            val text = "Qr code scanned successfully"
            val toast = Toast.makeText(context, text, Toast.LENGTH_SHORT)
            toast.show()

            // Cancel the toast after the specified duration
            Handler(Looper.getMainLooper()).postDelayed({ toast.cancel() }, 1000)

            if (itemId != null) {
              onQrScanned(itemId, user.uid)
            }
          }
        }
  }
}
