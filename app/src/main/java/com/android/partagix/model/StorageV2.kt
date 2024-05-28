package com.android.partagix.model

import android.net.Uri
import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger

class StorageV2 {
  // Cache for the images
  private val cache = mutableMapOf<String, File>()

  private fun getDefaultImages() {
    val storage = Firebase.storage
    val storageRef = storage.reference

    // Default item image
    val imageRef = storageRef.child("images/default-image.jpg")
    val localFile = File.createTempFile("default", ".tmp")
    imageRef.getFile(localFile).addOnSuccessListener { cache["default-image"] = localFile }

    // Default user image
    val userImageRef = storageRef.child("images/default-user-image.png")
    val userLocalFile = File.createTempFile("default-user", ".png")
    userImageRef.getFile(userLocalFile).addOnSuccessListener {
      cache["default-user-image"] = userLocalFile
    }
  }

  init {
    // Load the default images into the cache
    cache["default-image"] = File("still-loading.tmp")
    cache["default-user-image"] = File("still-loading2.tmp")
    getDefaultImages()
  }

  fun uploadImageToFirebaseStorage(
      imageUri: Uri,
      storage: FirebaseStorage = Firebase.storage,
      imageName: String = UUID.randomUUID().toString(),
      onSuccess: () -> Unit = {},
  ) {
    val storageRef = storage.reference

    // Create a reference to 'images/imageName.jpg'
    val imageRef = storageRef.child("images/$imageName.jpg")

    // Upload the file to Firebase Storage
    val uploadTask = imageRef.putFile(imageUri)
    cache.remove(imageName)

    // Register observers to listen for upload success or failure
    uploadTask
        .addOnSuccessListener { taskSnapshot -> onSuccess() }
        .addOnFailureListener { exception ->
          // Image upload failed
        }
  }

  fun getImageFromFirebaseStorage(
      p: String,
      storage: FirebaseStorage = Firebase.storage,
      onFailure: (exception: Exception) -> Unit = {},
      onSuccess: (localFile: File) -> Unit = {},
  ) {
    val prefix: String
    val path: String

    when (p) {
      "",
      "null",
      "default-image.jpg" -> {
        onSuccess(cache["default-image"]!!)
        return
      }
      "users/" -> {
        onSuccess(cache["default-user-image"]!!)
        return
      }
      else -> {
        path = "images/$p.jpg"
        prefix = "real"
      }
    }

    if (cache.containsKey(p)) {
      println("Cache hit for $p")
      onSuccess(cache[p]!!)
      return
    }

    // Get the image from Firebase Storage
    val storageRef = storage.reference

    // Create a reference to the image
    val imageRef = storageRef.child(path)

    // Download the image to a local file
    val localFile = File.createTempFile(prefix, ".tmp")
    imageRef
        .getFile(localFile)
        .addOnSuccessListener {
          // Local temp file has been created
          cache[p] = localFile
          onSuccess(localFile)
        }
        .addOnFailureListener {
          // Handle any errors
          Log.d("StorageV2", "Failed to get image from Firebase Storage : $path")
          onFailure(it)
          onSuccess(cache["default-image"]!!)
        }
  }

  fun getImagesFromFirebaseStorage(
      paths: List<String>,
      storage: FirebaseStorage = Firebase.storage,
      onFailure: (Exception) -> Unit = {},
      onSuccess: (List<File>) -> Unit = {},
  ) {
    val prefix = "real"
    val count = AtomicInteger(0)
    val res = Array(paths.size) { File("res/drawable/default_image.jpg") }
    for (i in paths.indices) {
      val path: String

      when (paths[i]) {
        "",
        "null",
        "default-image.jpg" -> {
          res[i] = cache["default-image"]!!
          if (count.incrementAndGet() == paths.size) {
            onSuccess(res.toList())
          }
          continue
        }
        "users/" -> {
          res[i] = cache["default-user-image"]!!
          if (count.incrementAndGet() == paths.size) {
            onSuccess(res.toList())
          }
          continue
        }
        else -> {
          path = "images/${paths[i]}.jpg"
        }
      }

      if (cache.containsKey(paths[i])) {
        res[i] = cache[paths[i]]!!
        if (count.incrementAndGet() == paths.size) {
          onSuccess(res.toList())
        }
        continue
      }

      // Get the image from Firebase Storage
      val storageRef = storage.reference

      // Create a reference to the image
      val imageRef = storageRef.child(path)

      // Download the image to a local file
      val localFile = File.createTempFile(prefix, ".tmp")
      imageRef
          .getFile(localFile)
          .addOnSuccessListener {
            // Local temp file has been created
            cache[paths[i]] = localFile
            res[i] = localFile
            if (count.incrementAndGet() == paths.size) {
              onSuccess(res.toList())
            }
          }
          .addOnFailureListener {
            res[i] = cache["default-image"]!!
            Log.d("StorageV2", "Failed to get image(s) from Firebase Storage : $path")
            onFailure(it)
            if (count.incrementAndGet() == paths.size) {
              onSuccess(res.toList())
            }
          }
    }
  }
}
