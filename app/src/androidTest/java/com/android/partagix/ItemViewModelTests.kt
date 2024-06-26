package com.android.partagix

import android.location.Location
import android.net.Uri
import com.android.partagix.model.Database
import com.android.partagix.model.ItemUIState
import com.android.partagix.model.ItemViewModel
import com.android.partagix.model.StorageV2
import com.android.partagix.model.category.Category
import com.android.partagix.model.emptyConst.emptyUser
import com.android.partagix.model.item.Item
import com.android.partagix.model.user.User
import com.android.partagix.model.visibility.Visibility
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.Firebase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.util.Executors
import com.google.firebase.firestore.util.Util
import com.google.firebase.storage.storage
import io.mockk.Runs
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.spyk
import java.io.File
import java.util.Calendar
import java.util.Date
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Test

class ItemViewModelTests {
  val emptyItem =
      Item(
          "",
          Category("", ""),
          "",
          "",
          Visibility.PUBLIC,
          1,
          Location(""),
          "",
          File("tempFile.tmp"))
  val itemWithID =
      Item(
          "8WuTkKJZLTAr6zs5L7rH",
          Category("0", "Category 1"),
          "test",
          "test",
          Visibility.PUBLIC,
          1,
          Location(""),
          imageId = File("noImage"))

  val itemWithIDmodified =
      Item(
          "8WuTkKJZLTAr6zs5L7rH",
          Category("0", "Category 1"),
          "modify",
          "modified",
          Visibility.PUBLIC,
          3,
          Location(""),
          imageId = File("noImage"))
  val itemNoID =
      Item("", Category("0", "Category 1"), "test", "test", Visibility.PUBLIC, 1, Location(""))

  /**
   * Test the updateUiState method Start with an empty item, update the UI state and check it's
   * indeed updated
   */
  @Test
  fun testUpdateUiState() {
    val db = mockk<Database>()
    val itemViewModel = ItemViewModel(db = db)

    itemViewModel.updateUiItem(itemWithID)
    itemViewModel.updateUiUser(emptyUser)
    assertEquals(itemWithID, itemViewModel.uiState.value.item)
    assertEquals(emptyUser, itemViewModel.uiState.value.user)
  }

  @Test
  fun initTest() {
    val item =
        Item(
            "idItem",
            Category("0", "Category 1"),
            "test",
            "test",
            Visibility.PUBLIC,
            1,
            Location(""),
            "idUser",
            File("tempFile.tmp"))
    val db = mockk<Database>()
    mockkStatic(File::class)
    every { File.createTempFile(any(), any()) } returns File("tempFile.tmp")
    every { db.getItemWithImage(any(), any()) } answers
        {
          val callback = args[1] as (Item) -> Unit
          callback(item)
        }

    every { db.getUserWithImage(any(), any(), any()) } answers
        {
          val callback = args[2] as (User) -> Unit
          callback(emptyUser)
        }

    val itemViewModel = ItemViewModel(item, db = db)

    assertEquals(item, itemViewModel.uiState.value.item)
    assertEquals(emptyUser, itemViewModel.uiState.value.user)

    val itemViewModel2 = ItemViewModel(item, id = "idItem", db = db)

    assertEquals(item, itemViewModel2.uiState.value.item)
    assertEquals(emptyUser, itemViewModel2.uiState.value.user)
  }

  /**
   * Test the save method Start with an empty item, save it and check it's saved Similar to create
   * an item
   */
  @Test
  fun testSaveNewItem() {
    val _uiState = MutableStateFlow(ItemUIState(emptyItem, emptyUser))
    val mockUiState: StateFlow<ItemUIState> = _uiState

    val taskCompletionSource = TaskCompletionSource<Void>()

    val mockCollection = mockk<CollectionReference>()

    val mockDocument = mockk<DocumentReference>()

    every { mockCollection.document(any()) } returns mockDocument

    every { mockCollection.document() } returns mockDocument

    val documentId = "wkUYnOmKkNVWlo1K8/59SDD/JtCWCf9MvnAgSYx9BbCN8ZbuNU+uSqPWVDuFnVRB"
    every { mockDocument.id } returns documentId

    every { mockDocument.set(any()) } returns
        taskCompletionSource.task.continueWith(
            Executors.DIRECT_EXECUTOR, Util.voidErrorTransformer())

    val mockDb: FirebaseFirestore = mockk {}

    every { mockDb.collection(any()) } returns mockCollection

    val mockQuerySnapshot = mockk<QuerySnapshot>()

    val task = mockk<Task<QuerySnapshot>>()
    every { task.result } returns mockQuerySnapshot

    every { task.addOnSuccessListener(any()) } returns task
    every { task.addOnFailureListener(any()) } returns task

    every { mockCollection.get() } returns task

    val db = spyk(Database(mockDb), recordPrivateCalls = true)

    every { db.getIdCategory(any(), any()) } answers
        {
          val callback = args[1] as (String) -> Unit
          callback("0")
        }
    every { db.createItem(any(), any()) } just Runs

    val itemViewModel = spyk(ItemViewModel(db = db))

    runBlocking {
      assert(mockUiState.value.item == emptyItem)
      itemViewModel.save(itemNoID)

      coVerify(exactly = 1) { itemViewModel.save(itemNoID) }
      coVerify(exactly = 1) { db.getIdCategory(any(), any()) }
      coVerify(exactly = 1) { db.createItem(any(), any(), any()) }
    }
  }

  /**
   * Test the save method Start with an item with an ID, modify it and save it, check it's indeed
   * been modify Similar to edit an item
   */
  @Test
  fun testSaveAnItem() {

    val taskCompletionSource = TaskCompletionSource<Void>()
    val mockCollection = mockk<CollectionReference>()
    val mockDocument = mockk<DocumentReference>()

    every { mockCollection.document(any()) } returns mockDocument

    val documentId = "wkUYnOmKkNVWlo1K8/59SDD/JtCWCf9MvnAgSYx9BbCN8ZbuNU+uSqPWVDuFnVRB"
    every { mockDocument.id } returns documentId

    every { mockDocument.set(any()) } returns
        taskCompletionSource.task.continueWith(
            Executors.DIRECT_EXECUTOR, Util.voidErrorTransformer())

    val mockDb: FirebaseFirestore = mockk {}

    every { mockDb.collection(any()) } returns mockCollection

    val mockQuerySnapshot = mockk<QuerySnapshot>()

    val task = mockk<Task<QuerySnapshot>>()
    every { task.result } returns mockQuerySnapshot

    every { task.addOnSuccessListener(any()) } returns task
    every { task.addOnFailureListener(any()) } returns task

    every { mockCollection.get() } returns task

    val db = spyk(Database(mockDb), recordPrivateCalls = true)

    val itemViewModel = spyk(ItemViewModel(itemWithID, db = db))

    runBlocking {
      assertEquals(itemWithID, itemViewModel.uiState.value.item)
      itemViewModel.save(itemWithIDmodified)
      itemViewModel.updateUiItem(itemWithIDmodified)

      coVerify(exactly = 1) { itemViewModel.save(itemWithIDmodified) }
      assertEquals(itemWithIDmodified, itemViewModel.uiState.value.item)
    }
  }

  @Test
  fun compareIDsTest() {
    val userId = "123"
    val itemId = "123"
    val itemId2 = "000"
    val itemViewModel = ItemViewModel()

    val result = itemViewModel.compareIDs(itemId, userId)
    val result2 = itemViewModel.compareIDs(itemId2, userId)

    assertTrue(result)
    assertFalse(result2)
  }

  @Test
  fun testGetAvailabilityDates() {
    val db = mockk<Database>()
    val itemViewModel = ItemViewModel(db = db)

    var calendar = Calendar.getInstance()
    calendar.set(2021, Calendar.MAY, 1, 0, 0, 0)

    every { db.getItemUnavailability(any(), any()) } answers
        {
          val callback = args[1] as (List<Date>) -> Unit
          callback(listOf(calendar.time))
        }
    itemViewModel.getAvailabilityDates()
    assertEquals(listOf(calendar.time), itemViewModel.uiState.value.unavailableDates)
  }

  @Test
  fun testImageHelpers() {
    val db = mockk<Database>()
    val storage = mockk<StorageV2>()
    val itemViewModel = ItemViewModel(db = db, imageStorage = storage)

    val uri = Uri.parse("content://media/external/images/media/1")
    val imageName = "test"
    val tempFile = File("tempFile.tmp")

    every { storage.uploadImageToFirebaseStorage(any(), any(), any(), any()) } answers
        {
          val callback = args[3] as () -> Unit
          callback()
        }
    every { storage.getImageFromFirebaseStorage(any(), any(), any(), any()) } answers
        {
          val callback = args[3] as (File) -> Unit
          callback(tempFile)
        }
    itemViewModel.uploadImage(uri, imageName) {}
    itemViewModel.updateImage(imageName) {}

    coVerify(exactly = 1) {
      storage.uploadImageToFirebaseStorage(uri, Firebase.storage, imageName, any())
    }
    coVerify(exactly = 1) {
      storage.getImageFromFirebaseStorage(imageName, Firebase.storage, any(), any())
    }
  }
}
