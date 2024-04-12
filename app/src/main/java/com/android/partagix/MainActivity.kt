package com.android.partagix.ui

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import com.android.partagix.resources.C
import com.android.partagix.ui.theme.PartagixAppTheme
import java.io.FileOutputStream
import java.io.IOException


private const val REQUEST_CODE_SAVE_FILE = 50

class MainActivity : ComponentActivity() {
  private lateinit var app: App

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    app = App(this)

    setContent {
      PartagixAppTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize().semantics { testTag = C.Tag.main_screen_container },
            color = MaterialTheme.colorScheme.background) {
              app.Create(this)
            }
      }
    }
  }

  @Deprecated("Deprecated in Java")
  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == REQUEST_CODE_SAVE_FILE && resultCode == RESULT_OK) {
      if (data != null && data.data != null) {
        val uri = data.data
        try {
          if (uri != null) {
            saveImageToUri(uri, data)
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
  private fun saveImageToUri(uri: Uri, data : Intent) {
    println("Saving image to $uri")
    val outputStream = contentResolver.openOutputStream(uri) as FileOutputStream?
    val qrByteArray = data.getByteArrayExtra(Intent.EXTRA_STREAM)
    println("qrByteArray: $qrByteArray")
    //outputStream?.write(qrByteArray) TODO : write qr code to output stream
    outputStream?.close()
  }

  companion object {
    private const val TAG = "Main"
  }
}
