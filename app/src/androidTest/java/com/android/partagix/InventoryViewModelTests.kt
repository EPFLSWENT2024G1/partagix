package com.android.partagix

import android.location.Location
import com.android.partagix.model.Database
import com.android.partagix.model.InventoryViewModel
import com.android.partagix.model.category.Category
import com.android.partagix.model.inventory.Inventory
import com.android.partagix.model.item.Item
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.loan.LoanState
import com.android.partagix.model.user.User
import com.android.partagix.model.visibility.Visibility
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.*
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.spyk
import java.util.Date
import kotlinx.coroutines.runBlocking
import org.junit.Test

class InventoryViewModelTests {
  val item1 =
      Item(
          "item1",
          Category("0", "Category 1"),
          "test",
          "description",
          Visibility.PUBLIC,
          1,
          Location(""),
          idUser = "8WuTkKJZLTAr6zs5L7rH")
  val item2 =
      Item(
          "item2",
          Category("0", "Category 2"),
          "modify",
          "description",
          Visibility.PUBLIC,
          32,
          Location(""),
          idUser = "8WuTkKJZLTAr6zs5L7rH")
  val item3 =
      Item(
          "item3",
          Category("0", "Category 3"),
          "oops",
          "description",
          Visibility.PUBLIC,
          4,
          Location(""),
          idUser = "8WuTkKJZLTAr6zs5L7rH")
  val item4 =
      Item(
          "item4",
          Category("0", "Category 3"),
          "oops",
          "description",
          Visibility.PUBLIC,
          4,
          Location(""),
          idUser = "8WuTkKJZLTAr6zs5L7rH")

  val user = User("8WuTkKJZLTAr6zs5L7rH", "user1", "", "", Inventory("", emptyList()))
  val loaned1 =
      Loan(
          "8WuTkKJZLTAr6zs5L7rH",
          "8WuTkKJZLTAr6zs5L7rH",
          "item1",
          Date(),
          Date(),
          "",
          "",
          "",
          "",
          LoanState.ACCEPTED)
  val loaned2 =
      Loan(
          "8WuTkKJZLTAr6zs5L7rH",
          "8WuTkKJZLTAr6zs5L7rH",
          "item2",
          Date(),
          Date(),
          "",
          "",
          "",
          "",
          LoanState.ACCEPTED)
  val loaned3 =
      Loan(
          "8WuTkKJZLTAr6zs5L7rH",
          "8WuTkKJZLTAr6zs5L7rH",
          "item3",
          Date(),
          Date(),
          "",
          "",
          "",
          "",
          LoanState.ACCEPTED)
  val list: List<Item> = listOf(item1, item2, item3)

  var onSuccessLoan: (List<Loan>) -> Unit = {}
  var onSuccess: (List<Item>) -> Unit = {}
  var onSuccessinv: (Inventory) -> Unit = {}
  @RelaxedMockK lateinit var fire: FirebaseAuth

  @Test
  fun testGetInventory() {
    val db = mockk<Database>()
    fire = mockk()
    every { db.getUserInventory(any(), any()) } answers
        {
          onSuccessinv = it.invocation.args[1] as (Inventory) -> Unit
          onSuccessinv(Inventory("", list))
        }

    every { db.getItems(any()) } answers
        {
          onSuccess = it.invocation.args[0] as (List<Item>) -> Unit
          onSuccess(list)
        }

    every { db.getUser(any(), any(), any()) } answers
        {
          val users = listOf(user, user, user)
          val onSuccessUs = it.invocation.args[2] as (User) -> Unit
          onSuccessUs(user)
        }
    every { db.getLoans(any()) } answers
        { invocation ->
          onSuccessLoan = invocation.invocation.args[0] as (List<Loan>) -> Unit
          onSuccessLoan(listOf(loaned1, loaned2, loaned3))
        }
    val inventoryViewModel = spyk(InventoryViewModel(db = db))
    runBlocking {
      assert(inventoryViewModel.uiState.value.borrowedItems == list)
      assert(inventoryViewModel.uiState.value.usersBor == listOf(user, user, user))
      assert(inventoryViewModel.uiState.value.loanBor == listOf(loaned1, loaned2, loaned3))
      assert(inventoryViewModel.uiState.value.items == list)
      assert(inventoryViewModel.uiState.value.users == listOf(user, user, user))
      assert(inventoryViewModel.uiState.value.loan == listOf(loaned1, loaned2, loaned3))
    }
  }

  @Test
  fun testInventoryNotNull() {
    val db = mockk<Database>()
    fire = mockk()
    every { db.getUserInventory(any(), any()) } answers
        {
          onSuccessinv = it.invocation.args[1] as (Inventory) -> Unit
          onSuccessinv(Inventory("", list))
        }

    every { db.getItems(any()) } answers
        {
          onSuccess = it.invocation.args[0] as (List<Item>) -> Unit
          onSuccess(list)
        }

    every { db.getUser(any(), any(), any()) } answers
        {
          val users = listOf(user, user, user)
          val onSuccessUs = it.invocation.args[2] as (User) -> Unit
          onSuccessUs(user)
        }
    every { db.getLoans(any()) } answers
        { invocation ->
          onSuccessLoan = invocation.invocation.args[0] as (List<Loan>) -> Unit
          onSuccessLoan(listOf(loaned1, loaned2, loaned3))
        }

    every { fire.currentUser } returns mockk { every { uid } returns "8WuTkKJZLTAr6zs5L7rH" }
    val inventoryViewModel = spyk(InventoryViewModel(db = db, firebaseAuth = fire))

    runBlocking {
      assert(inventoryViewModel.uiState.value.items == list)
      assert(inventoryViewModel.uiState.value.users == listOf(user, user, user))
      assert(inventoryViewModel.uiState.value.loan == listOf(loaned1, loaned2, loaned3))
      assert(inventoryViewModel.uiState.value.borrowedItems == list)
      assert(inventoryViewModel.uiState.value.usersBor == listOf(user, user, user))
      assert(inventoryViewModel.uiState.value.loanBor == listOf(loaned1, loaned2, loaned3))
    }
  }

  @Test
  fun testFindTime() {
    val db = mockk<Database>()
    every { db.getItems(any()) } answers
        {
          onSuccess = it.invocation.args[0] as (List<Item>) -> Unit
          onSuccess(list)
        }

    every { db.getUser(any(), any(), any()) } answers
        {
          val users = listOf(user, user, user)
          val onSuccessUs = it.invocation.args[2] as (User) -> Unit
          onSuccessUs(user)
        }
    every { db.getLoans(any()) } answers
        { invocation ->
          onSuccessLoan = invocation.invocation.args[0] as (List<Loan>) -> Unit
          onSuccessLoan(listOf(loaned1, loaned2, loaned3))
        }
    val inventoryViewModel = spyk(InventoryViewModel(db = db))
    inventoryViewModel.updateInv(list)
    inventoryViewModel.findTime(
        listOf(item4),
        { assert(it == Loan("", "", "", Date(), Date(), "", "", "", "", LoanState.CANCELLED)) })
  }

  @Test
  fun testFilterItems() {
    val db = mockk<Database>()
    every { db.getUserInventory(any(), any()) } answers
        {
          onSuccessinv = it.invocation.args[1] as (Inventory) -> Unit
          onSuccessinv(Inventory("", list))
        }

    every { db.getItems(any()) } answers
        {
          onSuccess = it.invocation.args[0] as (List<Item>) -> Unit
          onSuccess(list)
        }

    every { db.getUser(any(), any(), any()) } answers
        {
          val users = listOf(user, user, user)
          val onSuccessUs = it.invocation.args[2] as (User) -> Unit
          onSuccessUs(user)
        }
    every { db.getLoans(any()) } answers
        { invocation ->
          onSuccessLoan = invocation.invocation.args[0] as (List<Loan>) -> Unit
          onSuccessLoan(listOf(loaned1, loaned2, loaned3))
        }
    val inventoryViewModel = spyk(InventoryViewModel(db = db))
    inventoryViewModel.updateInv(list)
    inventoryViewModel.updateBor(list)
    inventoryViewModel.filterItems("Category 1")
    assert(inventoryViewModel.uiState.value.items == listOf(item1))
    inventoryViewModel.filterItems("32")
    assert(inventoryViewModel.uiState.value.items == listOf(item2))
    assert(inventoryViewModel.uiState.value.borrowedItems == listOf(item2))
    inventoryViewModel.filterItems("oops")
    assert(inventoryViewModel.uiState.value.items == listOf(item3))
    assert(inventoryViewModel.uiState.value.borrowedItems == listOf(item3))
    inventoryViewModel.filterItems("description")
    assert(inventoryViewModel.uiState.value.items == list)
    assert(inventoryViewModel.uiState.value.borrowedItems == list)
  }

  @Test
  fun testFilterItemsQuantity() {
    val db = mockk<Database>()
    every { db.getUserInventory(any(), any()) } answers
        {
          onSuccessinv = it.invocation.args[1] as (Inventory) -> Unit
          onSuccessinv(Inventory("", list))
        }

    every { db.getItems(any()) } answers
        {
          onSuccess = it.invocation.args[0] as (List<Item>) -> Unit
          onSuccess(list)
        }

    every { db.getUser(any(), any(), any()) } answers
        {
          val users = listOf(user, user, user)
          val onSuccessUs = it.invocation.args[2] as (User) -> Unit
          onSuccessUs(user)
        }
    every { db.getLoans(any()) } answers
        { invocation ->
          onSuccessLoan = invocation.invocation.args[0] as (List<Loan>) -> Unit
          onSuccessLoan(listOf(loaned1, loaned2, loaned3))
        }
    val inventoryViewModel = spyk(InventoryViewModel(db = db))
    inventoryViewModel.updateInv(list)
    assert(inventoryViewModel.uiState.value.items == list)
    inventoryViewModel.filterItems(3)
    assert(inventoryViewModel.uiState.value.items == listOf(item2, item3))
  }

  @Test
  fun testFilterItemsPosition() {
    val db = mockk<Database>()
    every { db.getUserInventory(any(), any()) } answers
        {
          onSuccessinv = it.invocation.args[1] as (Inventory) -> Unit
          onSuccessinv(Inventory("", list))
        }

    every { db.getItems(any()) } answers
        {
          onSuccess = it.invocation.args[0] as (List<Item>) -> Unit
          onSuccess(list)
        }

    every { db.getUser(any(), any(), any()) } answers
        {
          val users = listOf(user, user, user)
          val onSuccessUs = it.invocation.args[2] as (User) -> Unit
          onSuccessUs(user)
        }
    every { db.getLoans(any()) } answers
        { invocation ->
          onSuccessLoan = invocation.invocation.args[0] as (List<Loan>) -> Unit
          onSuccessLoan(listOf(loaned1, loaned2, loaned3))
        }
    val inventoryViewModel = spyk(InventoryViewModel(db = db))
    inventoryViewModel.updateInv(list)
    assert(inventoryViewModel.uiState.value.items == list)
    inventoryViewModel.filterItems(Location(""), 2.0)
    assert(inventoryViewModel.uiState.value.items == list)
  }
}
