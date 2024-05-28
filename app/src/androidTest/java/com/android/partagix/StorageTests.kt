package com.android.partagix

import android.net.Uri
import com.android.partagix.model.StorageV2
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import io.mockk.CapturingSlot
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import java.io.File
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotSame
import org.junit.After
import org.junit.Before
import org.junit.Test

class StorageTests {

  private lateinit var mockFirebaseStorage: FirebaseStorage
  private lateinit var mockStorageReference: StorageReference
  private lateinit var mockStorageReferenceDefault: StorageReference
  private lateinit var mockUploadTask: UploadTask
  private lateinit var mockDownTaskDefault: FileDownloadTask
  private lateinit var mockDownloadTaskSnapshotDefault: FileDownloadTask.TaskSnapshot

  private lateinit var mockDownloadTask: FileDownloadTask

  private lateinit var builtURI: Uri
  private lateinit var tempFile: CapturingSlot<File>

  @Before
  fun setUp() {
    // Mock FirebaseStorage and StorageReference
    mockFirebaseStorage = mockk<FirebaseStorage>()
    mockStorageReference = mockk<StorageReference>()
    mockStorageReferenceDefault = mockk<StorageReference>()
    mockDownTaskDefault = mockk<FileDownloadTask>()
    mockDownloadTaskSnapshotDefault = mockk<FileDownloadTask.TaskSnapshot>()
    mockUploadTask = mockk<UploadTask>()

    mockDownloadTask = mockk<FileDownloadTask>()

    // Mock storageReference.child("images") to return the mock StorageReference

    // Alternatively, you can construct a URI with components
    builtURI =
        Uri.Builder()
            .scheme("https") // Set the scheme (e.g., "https")
            .authority("example.com") // Set the authority (e.g., "example.com")
            .appendPath("path") // Append a path segment
            .appendQueryParameter("key", "value") // Add query parameters if needed
            .build()

    val imageName = UUID.randomUUID().toString()

    tempFile = slot<File>()

    // Storage references
    every { mockFirebaseStorage.reference } returns mockStorageReference
    every { mockStorageReference.child(any()) } returns mockStorageReference
    every { mockStorageReference.child("images/default-image.jpg") } returns
        mockStorageReferenceDefault
    every { mockStorageReference.child("images/default-user-image.png") } returns
        mockStorageReferenceDefault

    // Download task default
    every { mockStorageReferenceDefault.getFile(any<File>()) } returns mockDownTaskDefault
    every {
      mockDownTaskDefault.addOnSuccessListener(
          any<OnSuccessListener<FileDownloadTask.TaskSnapshot>>())
    } returns mockDownTaskDefault

    // Upload task
    every { mockStorageReference.putFile(builtURI) } returns mockUploadTask
    every { mockUploadTask.addOnSuccessListener(any()) } answers
        {
          val onSuccessListener = arg<OnSuccessListener<UploadTask.TaskSnapshot>>(0)
          val mockSnapshot = mockk<UploadTask.TaskSnapshot>()
          onSuccessListener.onSuccess(mockSnapshot)
          mockUploadTask
        }
    every { mockUploadTask.addOnFailureListener(any()) } answers
        {
          val onFailureListener = arg<OnFailureListener>(0)
          onFailureListener.onFailure(Exception("What a failure"))
          mockUploadTask
        }

    // Download task
    every { mockStorageReference.getFile(capture(tempFile)) } returns mockDownloadTask
    every { mockDownloadTask.addOnSuccessListener(any()) } answers
        {
          val onSuccessListener = arg<OnSuccessListener<FileDownloadTask.TaskSnapshot>>(0)
          val mockSnapshot = mockk<FileDownloadTask.TaskSnapshot>()
          onSuccessListener.onSuccess(mockSnapshot)
          mockDownloadTask
        }
    every { mockDownloadTask.addOnFailureListener(any()) } answers
        {
          val onFailureListener = arg<OnFailureListener>(0)
          onFailureListener.onFailure(Exception("What a failure"))
          mockDownloadTask
        }
  }

  @After
  fun tearDown() {
    tempFile.clear()
    clearAllMocks()
  }

  @Test
  fun uploadTest() {
    // Load the default image into the cache
    val storageV2 = StorageV2(mockFirebaseStorage)
    val latch = CountDownLatch(1)

    val onSuccessCallback = { latch.countDown() }

    storageV2.uploadImageToFirebaseStorage(
        imageUri = builtURI,
        storage = mockFirebaseStorage,
        imageName = "uploadTest",
        onSuccess = onSuccessCallback)

    assert(latch.await(5000, TimeUnit.MILLISECONDS))
  }

  @Test
  fun downloadTest1() {
    val storageV2 = StorageV2(mockFirebaseStorage)
    val latch = CountDownLatch(2)

    val onSuccessCallback = { res: File ->
      // Assert on the returned file
      if (res == tempFile.captured) {
        latch.countDown()
      }
    }
    val onFailureCallback = { _: Exception -> latch.countDown() }

    storageV2.getImageFromFirebaseStorage(
        "downloadTest",
        mockFirebaseStorage,
        onFailure = onFailureCallback,
        onSuccess = onSuccessCallback)

    assert(latch.await(5000, TimeUnit.MILLISECONDS))
  }

  @Test
  fun downloadTest2Success() {
    val storageV2 = StorageV2(mockFirebaseStorage)
    val latch = CountDownLatch(1)

    every { mockDownloadTask.addOnFailureListener(any()) } returns mockDownloadTask

    val onSuccessCallback = { res: List<File> ->
      // Assert on the returned file
      assertEquals(2, res.size)
      assertNotSame(res[0], res[1])
      latch.countDown()
    }

    storageV2.getImagesFromFirebaseStorage(
        listOf("downloadTest1", "downloadTest2"),
        mockFirebaseStorage,
        onSuccess = onSuccessCallback)

    assert(latch.await(5000, TimeUnit.MILLISECONDS))
  }

  @Test
  fun downloadTest2Failure() {
    val storageV2 = StorageV2(mockFirebaseStorage)
    val latch = CountDownLatch(3)

    every { mockDownloadTask.addOnSuccessListener(any()) } returns mockDownloadTask

    val onSuccessCallback = { res: List<File> ->
      // Assert on the returned file
      assertEquals(2, res.size)
      assertEquals(res[0], res[1])
      latch.countDown()
    }

    val onFailureCallback = { _: Exception -> latch.countDown() }
    storageV2.getImagesFromFirebaseStorage(
        listOf("downloadTest1", "downloadTest2"),
        mockFirebaseStorage,
        onFailure = onFailureCallback,
        onSuccess = onSuccessCallback)

    assert(latch.await(5000, TimeUnit.MILLISECONDS))
  }

  @Test
  fun downloadTest2EmptyString() {
    val storageV2 = StorageV2(mockFirebaseStorage)
    val latch = CountDownLatch(1)

    every { mockDownloadTask.addOnFailureListener(any()) } returns mockDownloadTask

    val onSuccessCallback = { res: List<File> ->
      // Assert on the returned file
      assertEquals(4, res.size)
      assertEquals(res[0], res[1])
      assertEquals(res[1], res[2])
      assertEquals(res[2], res[3])
      latch.countDown()
    }

    storageV2.getImagesFromFirebaseStorage(
        listOf("", "users/", "null", "default-image.jpg"),
        mockFirebaseStorage,
        onSuccess = onSuccessCallback)

    assert(latch.await(5000, TimeUnit.MILLISECONDS))
  }

  @Test
  fun downloadTestCacheHit() {
    val storageV2 = StorageV2(mockFirebaseStorage)
    val latch = CountDownLatch(1)

    every { mockDownloadTask.addOnFailureListener(any()) } returns mockDownloadTask

    var tempFile1 = File("")
    var tempFile2 = File("")

    val onSuccessCallback3 = { res: File ->
      // Assert on the returned file
      assertEquals(tempFile1, res)
      latch.countDown()
    }

    val onSuccessCallback2 = { res: List<File> ->
      // Assert on the returned file
      assertEquals(2, res.size)
      assertEquals(tempFile1, res[0])
      assertEquals(tempFile2, res[1])
      storageV2.getImageFromFirebaseStorage(
          "downloadTest1", mockFirebaseStorage, onSuccess = onSuccessCallback3)
    }

    val onSuccessCallback1 = { res: List<File> ->
      // Assert on the returned file
      assertEquals(2, res.size)
      tempFile1 = res[0]
      tempFile2 = res[1]

      storageV2.getImagesFromFirebaseStorage(
          listOf("downloadTest1", "downloadTest2"),
          mockFirebaseStorage,
          onSuccess = onSuccessCallback2)
    }

    storageV2.getImagesFromFirebaseStorage(
        listOf("downloadTest1", "downloadTest2"),
        mockFirebaseStorage,
        onSuccess = onSuccessCallback1)

    assert(latch.await(5000, TimeUnit.MILLISECONDS))
  }
}
