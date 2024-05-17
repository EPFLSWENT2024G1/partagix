import android.net.Uri
import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

// Function to upload image to Firebase Storage

/*
Example of Uri :
 val resourceId: Int = R.drawable.logo // Replace "your_image" with the name of your image resource
    val imageUri: Uri = Uri.Builder()
      .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
      .authority(resources.getResourcePackageName(resourceId))
      .appendPath(resources.getResourceTypeName(resourceId))
      .appendPath(resources.getResourceEntryName(resourceId))
      .build()
 */
fun uploadImageToFirebaseStorage(
    imageUri: Uri,
    storage: FirebaseStorage = Firebase.storage,
    imageName: String = UUID.randomUUID().toString(),
    onSuccess: (List<File>) -> Unit = {},
) {
  val storageRef = storage.reference

  // Create a reference to 'images/imageName.jpg'
  val imageRef = storageRef.child("images/$imageName.jpg")

  // Upload the file to Firebase Storage
  val uploadTask = imageRef.putFile(imageUri)

  // Register observers to listen for upload success or failure
  uploadTask
      .addOnSuccessListener { taskSnapshot ->
        // Image uploaded successfully
        onSuccess(listOf(File(imageUri.path!!.drop(7))))
      }
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
  if (p == "users/" || p == "" || p == "default-image.jpg") {
    onSuccess(File("res/drawable/default_image.jpg"))
    return
  } else {
    path = "images/$p.jpg"
    prefix = "real"
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
        onSuccess(localFile)
      }
      .addOnFailureListener {
        // Handle any errors
        Log.d("Image Download", "----- 1 Image download failed: $it")
        Log.d("Image Download", "$p -> $path -> $localFile")
        onFailure(it)
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
  val res = Array(paths.size) { File("drawable/default_image.jpg") }
  for (p in paths.indices) {
    val path: String
    if (paths[p] == "users/" || paths[p] == "" || paths[p] == "default-image.jpg") {
      if (count.incrementAndGet() == paths.size) {
        onSuccess(res.toList())
      }
      continue
    } else {
      path = "images/${paths[p]}.jpg"
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
          res[p] = localFile
          if (count.incrementAndGet() == paths.size) {
            onSuccess(res.toList())
          }
        }
        .addOnFailureListener {
          Log.d("Image Download", "----- Image download failed: $it")
          Log.d("Image Download", "${paths[p]} -> $path -> $localFile")
          onFailure(it)
        }
  }
}
