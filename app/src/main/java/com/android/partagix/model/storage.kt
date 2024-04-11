import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.util.*

// Function to upload image to Firebase Storage
fun uploadImageToFirebaseStorage(imageUri: Uri) {
  val storage = Firebase.storage
  val storageRef = storage.reference

  // Generate a random image name
  val imageName = UUID.randomUUID().toString()

  println("----- 1 $imageName")

  // Create a reference to 'images/imageName.jpg'
  val imageRef = storageRef.child("images/$imageName.jpg")

  println("----- 2 $imageRef")

  // Upload the file to Firebase Storage
  val uploadTask = imageRef.putFile(imageUri)

  println("----- 3 $uploadTask")

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

  println("----- 4")
}

fun getImageFromFirebaseStorage(path: String) {
  // Get the image from Firebase Storage
  val storage = Firebase.storage
  val storageRef = storage.reference

  // Create a reference to the image
  val imageRef = storageRef.child(path)

  // Download the image to a local file
  val localFile = File.createTempFile("images", "jpg")
  imageRef
      .getFile(localFile)
      .addOnSuccessListener {
        // Local temp file has been created
        println("----- Image downloaded successfully")
      }
      .addOnFailureListener {
        // Handle any errors
        println("----- Image download failed: $it")
      }
}