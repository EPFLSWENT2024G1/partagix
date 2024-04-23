package com.android.partagix

import android.location.Location
import com.android.partagix.model.Database
import com.android.partagix.model.ItemUIState
import com.android.partagix.model.ItemViewModel
import com.android.partagix.model.category.Category
import com.android.partagix.model.item.Item
import com.android.partagix.model.visibility.Visibility
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.util.Executors
import com.google.firebase.firestore.util.Util
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Test

class ItemViewModelTests {
  val emptyItem = Item("", Category("", ""), "", "", Visibility.PUBLIC, 1, Location(""))

  val itemWithID =
      Item(
          "8WuTkKJZLTAr6zs5L7rH",
          Category("0", "Category 1"),
          "test",
          "test",
          Visibility.PUBLIC,
          1,
          Location(""))

  val itemWithIDmodified =
      Item(
          "8WuTkKJZLTAr6zs5L7rH",
          Category("0", "Category 1"),
          "modify",
          "modified",
          Visibility.FRIENDS,
          3,
          Location(""))
  val itemNoID =
      Item("", Category("0", "Category 1"), "test", "test", Visibility.PUBLIC, 1, Location(""))

  @Test
  fun testUpdateUiState() {
    val _uiState = MutableStateFlow(ItemUIState(emptyItem))
    val mockUiState: StateFlow<ItemUIState> = _uiState
    val db = mockk<Database>()
    val itemViewModel = spyk(ItemViewModel(db = db))
    every { itemViewModel.uiState } returns mockUiState
    every { itemViewModel.updateUiState(itemWithID) } answers
        {
          _uiState.value = ItemUIState(itemWithID)
        }
    itemViewModel.updateUiState(itemWithID)
    assert(mockUiState.value.item == itemWithID)
  }

  @Test
  fun testSaveNewItem() {
    val _uiState = MutableStateFlow(ItemUIState(emptyItem))
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

    val itemViewModel = spyk(ItemViewModel(db = db))

    runBlocking {
      itemViewModel.save(itemNoID)

      coVerify(exactly = 1) { itemViewModel.save(itemNoID) }
    }
  }

  @Test
  fun testSaveAnItem() {
    val _uiState = MutableStateFlow(ItemUIState(itemWithID))
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

    val itemViewModel = spyk(ItemViewModel(itemWithID, db = db))

    every { itemViewModel.uiState } returns mockUiState

    every { itemViewModel.updateUiState(itemWithID) } answers
        {
          _uiState.value = ItemUIState(itemWithID)
        }

    every { itemViewModel.updateUiState(itemWithIDmodified) } answers
        {
          _uiState.value = ItemUIState(itemWithIDmodified)
        }

    runBlocking {
      itemViewModel.save(itemWithIDmodified)

      coVerify(exactly = 1) { itemViewModel.save(itemWithIDmodified) }
      assert(mockUiState.value.item == itemWithIDmodified)
    }
  }
}