package com.android.partagix

import android.content.Intent
import androidx.core.app.ActivityCompat.startActivityForResult
import com.android.partagix.model.CREATE_PNG_FILE
import com.android.partagix.model.StampViewModel
import com.android.partagix.ui.MainActivity
import io.mockk.Runs
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.spyk
import org.junit.Before
import org.junit.Test

class StampViewModelTests {
  private lateinit var mockContext: MainActivity

  private lateinit var viewModel: StampViewModel

  @Before
  fun setup() {
    mockContext = mockk(relaxed = true)
    viewModel = spyk(StampViewModel(mockContext))
  }

  @Test
  fun testGenerateQrCodeAndSave() {
    val itemId = "testItemId"
    val label = "testLabel"
    val small = "Small, XXcm x XXcm (XX per A4 page)"
    val med = "Medium, XXcm x XXcm (XX per A4 page)"
    val big = "Big, XXcm x XXcm (XX per A4 page)"
    val full = "Entire page, XXcm x XXcm (1 per A4 page)"
    val intent = slot<Intent>()

    every { mockContext.packageName } returns "com.android.partagix"
    every { mockContext.setQrBytes(any()) } just Runs
    every { startActivityForResult(mockContext, capture(intent), any(), any()) } just Runs

    viewModel.generateQRCodeAndSave(itemId, label, small)
    viewModel.generateQRCodeAndSave(itemId, label, med)
    viewModel.generateQRCodeAndSave(itemId, label, big)
    viewModel.generateQRCodeAndSave(itemId, label, full)

    assert(intent.captured.action == Intent.ACTION_CREATE_DOCUMENT)
    assert(intent.captured.type == "image/png")
    assert(intent.captured.getStringExtra(Intent.EXTRA_TITLE) == "qr-code.png")
    assert(intent.captured.categories.contains(Intent.CATEGORY_OPENABLE))

    coVerify(exactly = 4) { mockContext.setQrBytes(any()) }
    coVerify(exactly = 4) { startActivityForResult(mockContext, any(), CREATE_PNG_FILE, null) }
  }
}
