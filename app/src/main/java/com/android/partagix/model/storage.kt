import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.io.FileOutputStream
import java.util.*

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
    imageName: String = UUID.randomUUID().toString()
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
        println("----- Image uploaded successfully: ${taskSnapshot.metadata?.path}")
      }
      .addOnFailureListener { exception ->
        // Image upload failed
        println("----- Image upload failed: $exception")
      }
}

fun getImageFromFirebaseStorage(
    path: String,
    storage: FirebaseStorage = Firebase.storage,
    onSuccess: (localFile: File) -> Unit = {},
) {
  // Get the image from Firebase Storage
  val storageRef = storage.reference

  // Create a reference to the image
  val imageRef = storageRef.child(path)

  // Download the image to a local file
  val localFile = File.createTempFile("images", "jpg")
  imageRef
      .getFile(localFile)
      .addOnSuccessListener {
        // Local temp file has been created
        onSuccess(localFile)
      }
      .addOnFailureListener {
        // Handle any errors
        println("----- Image download failed: $it")
      }
}

fun getImagesFromFirebaseStorage(
    paths: List<String>,
    storage: FirebaseStorage = Firebase.storage,
    onSuccess: (List<File>) -> Unit = {},
) {
  var count = 0
  if (paths.isNotEmpty()) {
    getImageFromFirebaseStorage(paths[count]) { localFile ->
      val localFiles = mutableListOf(localFile)
      count++
      if (count == paths.size) {
        onSuccess(localFiles)
      } else {
        getImagesFromFirebaseStorage(paths.subList(count, paths.size), storage) { files ->
          localFiles.addAll(files)
          onSuccess(localFiles)
        }
      }
    }
  }
}