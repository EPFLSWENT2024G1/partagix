import android.net.Uri
import android.widget.Toast
import com.google.common.base.Verify.verify
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import java.io.File
import java.util.*
import java.util.concurrent.CountDownLatch
import junit.framework.TestCase
import org.junit.Before
import org.junit.Test

class FirebaseStorageTest {


  @Test
  fun testUploadImageToFirebaseStorage() {
    // Mock FirebaseStorage and StorageReference
    val firebaseStorage = mockk<FirebaseStorage>()
    val storageReference = mockk<StorageReference>()
    val uploadTask = mockk<UploadTask>()
    // Mock storageReference.child("images") to return the mock StorageReference

    // Mock UUID generation
    val uri = Uri.parse("content://com.example/mydata/123")

    // Alternatively, you can construct a URI with components
    val builtUri =
        Uri.Builder()
            .scheme("https") // Set the scheme (e.g., "https")
            .authority("example.com") // Set the authority (e.g., "example.com")
            .appendPath("path") // Append a path segment
            .appendQueryParameter("key", "value") // Add query parameters if needed
            .build()

    val imageName = UUID.randomUUID().toString()

    every { firebaseStorage.reference } returns storageReference
    every { storageReference.child("images/$imageName.jpg") } returns storageReference
    every { storageReference.putFile(builtUri) } returns uploadTask

    every { uploadTask.addOnSuccessListener(any()) } returns uploadTask
    every { uploadTask.addOnFailureListener(any()) } returns uploadTask

    // Call the function to be tested
    uploadImageToFirebaseStorage(builtUri, firebaseStorage, imageName)
    verify { storageReference.child(any()) }
    verify { storageReference.putFile(builtUri) }
  }

  @Test
  fun testDownloadFromFirebaseStorage() {
    // Mock FirebaseStorage and StorageReference
    val firebaseStorage = mockk<FirebaseStorage>()
    val storageReference = mockk<StorageReference>()
    val fileDownloadTask = mockk<FileDownloadTask>()

    val path = "123.jpg"
    val localFile = File.createTempFile("local", ".tmp")

    every { firebaseStorage.reference } returns storageReference
    every { storageReference.child("images/$path.jpg") } returns storageReference
    every { storageReference.getFile(any<File>()) } returns fileDownloadTask

    every { fileDownloadTask.addOnSuccessListener(any()) } returns fileDownloadTask
    every { fileDownloadTask.addOnFailureListener(any()) } returns fileDownloadTask

    getImageFromFirebaseStorage(path, firebaseStorage)

    verify { storageReference.child("images/$path.jpg") }

    verify { storageReference.getFile(any<File>()) }
  }

  @Test
  fun testDownloadFromFirebaseStorageEmptyString() {
    // Mock FirebaseStorage and StorageReference
    val firebaseStorage = mockk<FirebaseStorage>()
    val storageReference = mockk<StorageReference>()
    val fileDownloadTask = mockk<FileDownloadTask>()

    val path = ""
    val localFile = File.createTempFile("local", ".tmp")

    val latch = CountDownLatch(1)

    val onSuccessCallback: (File) -> Unit = { res ->
      // Assert on the returned file
      TestCase.assertEquals(File("res/drawable/default_image.jpg"), res)
      latch.countDown()
    }

    every { firebaseStorage.reference } returns storageReference
    every { storageReference.child("images/default-image.jpg") } returns storageReference
    every { storageReference.getFile(any<File>()) } returns fileDownloadTask

    every { fileDownloadTask.addOnSuccessListener(any()) } returns fileDownloadTask
    every { fileDownloadTask.addOnFailureListener(any()) } returns fileDownloadTask

    getImageFromFirebaseStorage(path, firebaseStorage, onSuccess = onSuccessCallback)
    latch.await()
  }
}
