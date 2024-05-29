import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.partagix.model.auth.Authentication
import com.google.mlkit.vision.barcode.common.Barcode
import io.mockk.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BarcodeAnalyzerTest {

  private lateinit var context: Context
  private lateinit var onQrScanned: (String, String) -> Unit
  private lateinit var barcodeAnalyzer: BarcodeAnalyzer

  @Before
  fun setUp() {
    context = mockk(relaxed = true)
    onQrScanned = mockk(relaxed = true)
    barcodeAnalyzer = BarcodeAnalyzer(context, onQrScanned)
  }

  @Test
  fun testOnSuccess() {
    val itemId = "testItemId"
    val userUid = "testUserUid"
    val raw = "test://example?itemId=$itemId"

    // Mock the Authentication.getUser() to return a test user with a specific UID
    mockkObject(Authentication)
    every { Authentication.getUser() } returns mockk { every { uid } returns userUid }

    // Mock the barcode to return the rawValue
    val barcode = mockk<Barcode> { every { rawValue } returns raw }
    val barcodes = listOf(barcode)

    // Mock the Uri parsing
    mockkStatic(Uri::class)
    every { Uri.parse(raw) } returns mockk { every { getQueryParameter("itemId") } returns itemId }

    val toast = mockk<Toast>()
    every { toast.show() } just Runs
    // Mock Toast
    mockkStatic(Toast::class)
    every { Toast.makeText(context, any<String>(), any()) } returns toast

    // Call the onSuccess method
    barcodeAnalyzer.onSuccessBarcode(barcodes)

    // Verify the onQrScanned is called with the correct parameters
    verify { onQrScanned(itemId, userUid) }

    // Unmock all
    unmockkAll()
  }
}
