package com.android.partagix

import android.location.Location
import com.android.partagix.model.Database
import com.android.partagix.model.InventoryViewModel
import com.android.partagix.model.auth.Authentication
import com.android.partagix.model.category.Category
import com.android.partagix.model.inventory.Inventory
import com.android.partagix.model.item.Item
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.loan.LoanState
import com.android.partagix.model.user.User
import com.android.partagix.model.visibility.Visibility
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.spyk
import java.io.File
import java.util.Date
import java.util.concurrent.CountDownLatch
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test

class InventoryViewModelTests {
  val db = mockk<Database>()
  val item1 =
      Item(
          "item1",
          Category("0", "Category 1"),
          "test",
          "description",
          Visibility.PUBLIC,
          1,
          Location(""),
          idUser = "8WuTkKJZLTAr6zs5L7rH",
          imageId = File("image1"))

  val item2 =
      Item(
          "item2",
          Category("0", "Category 2"),
          "modify",
          "description",
          Visibility.PUBLIC,
          32,
          Location(""),
          idUser = "8WuTkKJZLTAr6zs5L7rH",
          imageId = File("image2"))
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
          "",
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
          "",
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
          "",
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
  var onSuccess1Item: (Item) -> Unit = {}
  var onSuccessinv: (Inventory) -> Unit = {}
  @RelaxedMockK lateinit var fire: FirebaseAuth

  @Before
  fun setUp() {
    fire = mockk()
    val firebaseUser = mockk<FirebaseUser>()
    every { firebaseUser.uid } returns "8WuTkKJZLTAr6zs5L7rH"
    every { fire.currentUser } returns firebaseUser
    every { this@InventoryViewModelTests.fire.currentUser } returns firebaseUser
    every { db.getUserInventory(any(), any()) } answers
        {
          onSuccessinv = it.invocation.args[1] as (Inventory) -> Unit
          onSuccessinv(Inventory("", list))
        }

    every { db.getItemsWithImages(any()) } answers
        {
          onSuccess = it.invocation.args[0] as (List<Item>) -> Unit
          onSuccess(list)
        }
    every { db.getItemWithImage(any(), any()) } answers
        {
          onSuccess1Item = it.invocation.args[0] as (Item) -> Unit
          onSuccess1Item(item1)
        }

    every { db.getUsers(any()) } answers
        {
          val onSuccessUs = it.invocation.args[0] as (List<User>) -> Unit
          onSuccessUs(listOf(user))
        }

    every { db.getUser(any(), any(), any()) } answers
        {
          val onSuccessUs = it.invocation.args[2] as (User) -> Unit
          onSuccessUs(user)
        }
    every { db.getLoans(any()) } answers
        { invocation ->
          onSuccessLoan = invocation.invocation.args[0] as (List<Loan>) -> Unit
          onSuccessLoan(listOf(loaned1, loaned2, loaned3))
        }
    every { this@InventoryViewModelTests.fire.currentUser } returns
        mockk { every { uid } returns "8WuTkKJZLTAr6zs5L7rH" }
  }

  @After
  fun tearDown() {
    clearAllMocks()
  }

  @Test
  fun testGetInventory() {
    val latch = CountDownLatch(1)
    val inventoryViewModel = spyk(InventoryViewModel(db = db, latch = latch, firebaseAuth = fire))
    latch.await()
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

    val mockUser = mockk<FirebaseUser>()
    mockkObject(Authentication)
    every { Authentication.getUser() } returns mockUser
    every { mockUser.uid } returns "8WuTkKJZLTAr6zs5L7rH"
    val latch = CountDownLatch(1)
    val inventoryViewModel = spyk(InventoryViewModel(db = db, firebaseAuth = fire, latch = latch))
    latch.await()

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
    val latch = CountDownLatch(1)
    val inventoryViewModel = spyk(InventoryViewModel(db = db, latch = latch))
    latch.await()
    inventoryViewModel.updateInv(list)
    inventoryViewModel.findTime(
        listOf(item4),
        { assert(it == Loan("", "", "", "", Date(), Date(), "", "", "", "", LoanState.CANCELLED)) })
  }

  @Test
  fun testFilterItems() {
    val latch = CountDownLatch(1)
    val inventoryViewModel = spyk(InventoryViewModel(db = db, latch = latch))
    latch.await()
    inventoryViewModel.updateInv(list)
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
    val latch = CountDownLatch(1)
    val inventoryViewModel = spyk(InventoryViewModel(db = db, latch = latch))
    latch.await()
    inventoryViewModel.updateInv(list)
    assert(inventoryViewModel.uiState.value.items == list)
    inventoryViewModel.filterItems(3)
    assert(inventoryViewModel.uiState.value.items == listOf(item2, item3))
  }
}
