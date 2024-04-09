import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
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
  uploadTask.addOnSuccessListener { taskSnapshot ->
    // Image uploaded successfully
    println("----- Image uploaded successfully: ${taskSnapshot.metadata?.path}")
  }.addOnFailureListener { exception ->
    // Image upload failed
    println("----- Image upload failed: $exception")
  }

  println("----- 4")
}

// Example usage:
// Assuming 'imageUri' is the URI of the image you want to upload
// You need to pass the URI of the image as a parameter to the function
// uploadImageToFirebaseStorage(imageUri)

val storageRef = Firebase.storage.reference

