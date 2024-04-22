package com.android.partagix

import android.location.Location
import com.android.partagix.model.Database
import com.android.partagix.model.ItemUIState
import com.android.partagix.model.ItemViewModel
import com.android.partagix.model.category.Category
import com.android.partagix.model.item.Item
import com.android.partagix.model.visibility.Visibility
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.spyk
import io.mockk.unmockkStatic
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
        val db = mockk<Database>()
        val itemViewModel = spyk(ItemViewModel(db = db))
        val item = Item("", Category("", ""), "", "", Visibility.PUBLIC, 1, Location(""))
        itemViewModel.save(item)
        assert(itemViewModel.uiState.value.item == item)
    }

}

