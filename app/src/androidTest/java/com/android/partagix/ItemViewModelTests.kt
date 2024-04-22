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
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.util.Executors
import com.google.firebase.firestore.util.Util
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.spyk
import io.mockk.unmockkStatic
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.sql.Date

class ItemViewModelTests {
    val emptyItem = Item("", Category("", ""), "", "", Visibility.PUBLIC, 1, Location(""))
    private val _uiState = MutableStateFlow(ItemUIState(emptyItem))
    val mockUiState: StateFlow<ItemUIState> = _uiState

    val itemWithID = Item("8WuTkKJZLTAr6zs5L7rH", Category("0", "Category 1"), "test", "test", Visibility.PUBLIC, 1, Location(""))
    val itemNoID = Item("", Category("0", "Category 1"), "test", "test", Visibility.PUBLIC, 1, Location(""))
    @Test
    fun testUpdateUiState() {
        val db = mockk<Database>()
        val itemViewModel = spyk(ItemViewModel(db = db))
        every { itemViewModel.uiState } returns mockUiState
        every { itemViewModel.updateUiState(itemWithID) } answers {_uiState.value = ItemUIState(itemWithID)}
        itemViewModel.updateUiState(itemWithID)
        assert(mockUiState.value.item == itemWithID)

    }

    @Test
    fun testSaveNewItem() {
        val db = mockk<Database>()
        val itemViewModel = spyk(ItemViewModel(db = db))

        every {db.getIdCategory(itemNoID.category.name){
            db.createItem(
                "userId",
                Item(
                    itemNoID.id,
                    Category(it, itemNoID.category.name),
                    itemNoID.name,
                    itemNoID.description,
                    itemNoID.visibility,
                    itemNoID.quantity,
                    itemNoID.location))}} answers {db.createItem("userId",itemNoID)}

        itemViewModel.save(itemNoID)

        coVerify(exactly = 1) {db.createItem("userId",itemNoID)}
    }

    @Test
    fun testSaveAnItem(){
        val taskCompletionSource = TaskCompletionSource<Void>()

        val mockCollection = mockk<CollectionReference>()

        val mockDocument = mockk<DocumentReference>()

        every { mockCollection.document(any()) } returns mockDocument

        every { mockCollection.document() } returns mockDocument

        val documentId = "wkUYnOmKkNVWlo1K8/59SDD/JtCWCf9MvnAgSYx9BbCN8ZbuNU+uSqPWVDuFnVRB"
        every { mockDocument.id } returns documentId

        every { mockDocument.set(any()) } returns
                taskCompletionSource.task.continueWith(Executors.DIRECT_EXECUTOR,
                    Util.voidErrorTransformer()
                )

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

        every { itemViewModel.uiState } returns mockUiState
        every { itemViewModel.updateUiState(itemWithID) } answers {_uiState.value = ItemUIState(itemWithID)}
        println("---- $_uiState.value.item")

        runBlocking {
            itemViewModel.save(itemWithID)

            coVerify(exactly = 1) { itemViewModel.save(itemWithID) }
            //assert(mockUiState.value.item == itemWithID)
        }

        println("-------- $_uiState.value.item")


    }

}

