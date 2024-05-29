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
          .addOnSuccessListener { barcode -> onSuccessBarcode(barcode) }
          .addOnCompleteListener { imageProxy.close() }
    }
  }

  fun onSuccessBarcode(barcode: List<Barcode>) {
    barcode
        ?.takeIf { it.isNotEmpty() }
        ?.mapNotNull { it.rawValue }
        ?.joinToString(",")
        ?.let {
          val user = Authentication.getUser()
          if (user != null) {
            val uri = Uri.parse(it)
            val itemId = uri.getQueryParameter("itemId")

            if (itemId != null) {
              onQrScanned(itemId, user.uid)
            }
          }
        }
  }
}
