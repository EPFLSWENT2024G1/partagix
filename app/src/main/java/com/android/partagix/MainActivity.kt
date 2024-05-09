package com.android.partagix

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import com.android.partagix.model.CREATE_PNG_FILE
import com.android.partagix.ui.App
import com.android.partagix.ui.theme.PartagixAppTheme
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : ComponentActivity() {
  private lateinit var app: App
  private var qrBytes: ByteArray? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    var itemId: String? = null
    if (intent.action == Intent.ACTION_VIEW) {
      val uri: Uri? = intent.data
      if (uri != null) {
        // Handle the URI, extract additional data if needed
        itemId = uri.getQueryParameter("itemId")
        println("----- itemId: $itemId")
        // Perform actions based on the itemId or other parameters

      }
    }
    app = App(this)

    setContent {
      PartagixAppTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize().semantics {},
            color = MaterialTheme.colorScheme.background) {
              app.Create(itemId)
            }
      }
    }
  }

  fun myInitializationFunction(route: String) {
    app.navigateForTest(route)
  }

  fun setQrBytes(qrBytes: ByteArray) {
    this.qrBytes = qrBytes
  }

  @Deprecated("Deprecated in Java")
  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == CREATE_PNG_FILE && resultCode == RESULT_OK) {
      if (data != null && data.data != null) {
        val uri = data.data
        try {
          if (uri != null && qrBytes != null) {
            saveImageToUri(uri, qrBytes!!)
            Toast.makeText(this, "Image saved successfully", Toast.LENGTH_SHORT).show()
          } else {
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show()
          }
        } catch (e: IOException) {
          e.printStackTrace()
          Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show()
        }
      }
    }
  }

  @Throws(IOException::class)
  private fun saveImageToUri(uri: Uri, data: ByteArray) {
    val outputStream = contentResolver.openOutputStream(uri) as FileOutputStream?
    outputStream?.write(data)
    outputStream?.close()
  }

  companion object {
    private const val TAG = "Main"
  }

  @Composable
  fun Create(id: String?) {
    Column { Text("Hello, World! $id") }
  }
}
