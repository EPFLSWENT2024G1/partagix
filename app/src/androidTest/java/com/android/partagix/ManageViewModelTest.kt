package com.android.partagix

import android.location.Location
import com.android.partagix.model.Database
import com.android.partagix.model.ManageLoanViewModel
import com.android.partagix.model.auth.Authentication
import com.android.partagix.model.category.Category
import com.android.partagix.model.inventory.Inventory
import com.android.partagix.model.item.Item
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.loan.LoanState
import com.android.partagix.model.user.User
import com.android.partagix.model.visibility.Visibility
import com.google.firebase.auth.FirebaseUser
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.spyk
import java.util.Date
import java.util.concurrent.CountDownLatch
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test

class ManageViewModelTest {
  private val db = mockk<Database>()

  private val item1 =
      Item(
          "item1",
          Category("0", "Category 1"),
          "test",
          "description",
          Visibility.PUBLIC,
          1,
          Location(""),
          idUser = "8WuTkKJZLTAr6zs5L7rH")

  private val itemz =
      Item(
          "item1",
          Category("0", "Category 1"),
          "test",
          "description",
          Visibility.PUBLIC,
          1,
          Location(""),
          idUser = "zB8N1tJRmKcNI6AvawMWIRp66wA")

  private val user =
      User("8WuTkKJZLTAr6zs5L7rH", "user1", "", "", Inventory("8WuTkKJZLTAr6zs5L7rH", emptyList()))

  private val userz =
      User(
          "zB8N1tJRmKcNI6AvawMWIRp66wA",
          "user1",
          "",
          "",
          Inventory("zB8N1tJRmKcNI6AvawMWIRp66wA", emptyList()))

  private val loan1 =
      Loan(
          id = "1",
          commentBorrower = "commentLoaner",
          commentLender = "commentOwner",
          endDate = Date(),
          idItem = "item1",
          idLender = "8WuTkKJZLTAr6zs5L7rH",
          idBorrower = "8WuTkKJZLTAr6zs5L7rH",
          reviewBorrower = "reviewLoaner",
          reviewLender = "reviewOwner",
          startDate = Date(),
          state = LoanState.PENDING)
  private val loan2 =
      Loan(
          id = "2",
          commentBorrower = "commentLoaner",
          commentLender = "commentOwner",
          endDate = Date(),
          idItem = "item1",
          idLender = "8WuTkKJZLTAr6zs5L7rH",
          idBorrower = "zB8N1tJRmKcNI6AvawMWIRp66wA",
          reviewBorrower = "reviewLoaner",
          reviewLender = "reviewOwner",
          startDate = Date(),
          state = LoanState.PENDING)

  private val loan3 =
      Loan(
          id = "3",
          commentBorrower = "commentLoaner",
          commentLender = "commentOwner",
          endDate = Date(),
          idItem = "item1",
          idLender = "8WuTkKJZLTAr6zs5L7rH",
          idBorrower = "zB8N1tJRmKcNI6AvawMWIRp66wA",
          reviewBorrower = "reviewLoaner",
          reviewLender = "reviewOwner",
          startDate = Date(),
          state = LoanState.PENDING)

  @Before
  fun setup() {
    clearAllMocks()
    every { db.getItems(any()) } answers
        {
          firstArg<(List<Item>) -> Unit>().invoke(listOf(item1, itemz, itemz))
        }
    every { db.getItemsWithImages(any()) } answers
        {
          firstArg<(List<Item>) -> Unit>().invoke(listOf(item1, itemz, itemz))
        }
    every { db.getUser(any(), any(), any()) } answers { thirdArg<(User) -> Unit>().invoke(user) }
    every { db.getLoans(any()) } answers
        {
          firstArg<(List<Loan>) -> Unit>().invoke(listOf(loan1, loan2, loan3))
        }
    every { db.setLoan(any()) } just Runs
  }

  @After
  fun tearDown() {
    clearAllMocks()
  }

  @Test
  fun testAcceptAndDecline() {
    val mockUser = mockk<FirebaseUser>()
    mockkObject(Authentication)
    every { Authentication.getUser() } returns mockUser
    every { mockUser.uid } returns "8WuTkKJZLTAr6zs5L7rH"

    val latch = CountDownLatch(1)
    val manageViewModel = spyk(ManageLoanViewModel(db = db, latch = latch))
    latch.await()

    manageViewModel.update(
        listOf(item1, item1, item1),
        listOf(user, user, user),
        listOf(loan1, loan2, loan3),
        listOf(false, false, false))
    manageViewModel.acceptLoan(loan1, 0)

    assert(manageViewModel.uiState.value.items == listOf(item1, item1))
    assert(manageViewModel.uiState.value.loans == listOf(loan2, loan3))
    assert(manageViewModel.uiState.value.users == listOf(user, user))
    assert(manageViewModel.uiState.value.expanded == listOf(false, false))

    manageViewModel.declineLoan(loan3, 1)

    assert(manageViewModel.uiState.value.items == listOf(item1))
    assert(manageViewModel.uiState.value.loans == listOf(loan2))
    assert(manageViewModel.uiState.value.users == listOf(user))
    assert(manageViewModel.uiState.value.expanded == listOf(false))
  }

  @Test
  fun testGetLoanRequestsNoUser() {
    mockkObject(Authentication)
    every { Authentication.getUser() } returns null

    val latch = CountDownLatch(1)
    val manageViewModel = spyk(ManageLoanViewModel(db = db, latch = latch))
    latch.await()

    runBlocking {
      assert(manageViewModel.uiState.value.users.isEmpty())
      assert(manageViewModel.uiState.value.items.isEmpty())
      assert(manageViewModel.uiState.value.loans.isEmpty())
      assert(manageViewModel.uiState.value.expanded.isEmpty())
    }
  }

  @Test
  fun testGetLoanRequestsUserNotNull() {
    val mockUser = mockk<FirebaseUser>()
    mockkObject(Authentication)
    every { Authentication.getUser() } returns mockUser
    every { mockUser.uid } returns "8WuTkKJZLTAr6zs5L7rH"

    val latch = CountDownLatch(1)
    val manageViewModel = spyk(ManageLoanViewModel(db = db, latch = latch))
    latch.await()

    runBlocking {
      assert(manageViewModel.uiState.value.items == listOf(item1, item1, item1))
      assert(manageViewModel.uiState.value.users == listOf(user, user, user))
      assert(manageViewModel.uiState.value.loans == listOf(loan1, loan2, loan3))
      assert(manageViewModel.uiState.value.expanded == listOf(false, false, false))
    }
  }

  @Test
  fun testGetOutgoingLoanRequests() {
    val mockUser = mockk<FirebaseUser>()
    mockkObject(Authentication)
    every { Authentication.getUser() } returns mockUser
    every { mockUser.uid } returns "zB8N1tJRmKcNI6AvawMWIRp66wA"
    every { db.getUser(any(), any(), any()) } answers { thirdArg<(User) -> Unit>().invoke(userz) }

    val latch = CountDownLatch(1)
    val manageViewModel = spyk(ManageLoanViewModel(db = db))
    manageViewModel.getLoanRequests(latch = latch, isOutgoing = true)
    latch.await()

    runBlocking {
      assert(manageViewModel.uiState.value.items == listOf(item1, item1))
      assert(manageViewModel.uiState.value.users == listOf(userz, userz))
      assert(manageViewModel.uiState.value.loans == listOf(loan2, loan3))
      assert(manageViewModel.uiState.value.expanded == listOf(false, false))
    }
  }
}
