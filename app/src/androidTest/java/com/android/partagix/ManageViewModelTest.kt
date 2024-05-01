package com.android.partagix

import android.location.Location
import com.android.partagix.model.Database
import com.android.partagix.model.ManageLoanViewModel
import com.android.partagix.model.category.Category
import com.android.partagix.model.inventory.Inventory
import com.android.partagix.model.item.Item
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.loan.LoanState
import com.android.partagix.model.user.User
import com.android.partagix.model.visibility.Visibility
import com.google.firebase.auth.FirebaseAuth
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.spyk
import java.util.Date
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class ManageViewModelTest {
  val db = mockk<Database>()
  @RelaxedMockK lateinit var fire: FirebaseAuth
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

  val user =
      User("8WuTkKJZLTAr6zs5L7rH", "user1", "", "", Inventory("8WuTkKJZLTAr6zs5L7rH", emptyList()))
  val loan1 =
      Loan(
          id = "1",
          commentLoaner = "commentLoaner",
          commentOwner = "commentOwner",
          endDate = Date(),
          idItem = "item1",
          idOwner = "8WuTkKJZLTAr6zs5L7rH",
          idLoaner = "8WuTkKJZLTAr6zs5L7rH",
          reviewLoaner = "reviewLoaner",
          reviewOwner = "reviewOwner",
          startDate = Date(),
          state = LoanState.PENDING)
  val loan2 =
      Loan(
          id = "2",
          commentLoaner = "commentLoaner",
          commentOwner = "commentOwner",
          endDate = Date(),
          idItem = "item1",
          idOwner = "8WuTkKJZLTAr6zs5L7rH",
          idLoaner = "8WuTkKJZLTAr6zs5L7rH",
          reviewLoaner = "reviewLoaner",
          reviewOwner = "reviewOwner",
          startDate = Date(),
          state = LoanState.PENDING)
  val loan3 =
      Loan(
          id = "3",
          commentLoaner = "commentLoaner",
          commentOwner = "commentOwner",
          endDate = Date(),
          idItem = "item1",
          idOwner = "8WuTkKJZLTAr6zs5L7rH",
          idLoaner = "8WuTkKJZLTAr6zs5L7rH",
          reviewLoaner = "reviewLoaner",
          reviewOwner = "reviewOwner",
          startDate = Date(),
          state = LoanState.PENDING)

  @Before
  fun setup() {
    fire = mockk()
    every { fire.currentUser } returns mockk { every { uid } returns "8WuTkKJZLTAr6zs5L7rH" }
    every { db.getItems(any()) } answers
        {
          firstArg<(List<Item>) -> Unit>().invoke(listOf(item1, item1, item1))
        }
    every { db.getUser(any(), any(), any()) } answers { thirdArg<(User) -> Unit>().invoke(user) }
    every { db.getLoans(any()) } answers
        {
          firstArg<(List<Loan>) -> Unit>().invoke(listOf(loan1, loan2, loan3))
        }
  }

  @Test
  fun testAcceptAndDecline() {
    val manageViewModel = spyk(ManageLoanViewModel(db = db))
    every { db.setLoan(any()) } just Runs
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
  fun testGetLoanRequests() {
    val db = mockk<Database>()
    fire = mockk()
    every { db.getItems(any()) } answers
        {
          firstArg<(List<Item>) -> Unit>().invoke(listOf(item1, item1, item1))
        }
    every { db.getUser(any(), any(), any()) } answers { thirdArg<(User) -> Unit>().invoke(user) }
    every { db.getLoans(any()) } answers
        {
          firstArg<(List<Loan>) -> Unit>().invoke(listOf(loan1, loan2, loan3))
        }

    // val latch = CountDownLatch(1)

    val manageViewModel = spyk(ManageLoanViewModel(db = db))

    // latch.await()
    runBlocking {
      println(manageViewModel.uiState.value.items)
      assert(manageViewModel.uiState.value.items == listOf(item1, item1, item1))
      assert(manageViewModel.uiState.value.users == listOf(user, user, user))
      assert(manageViewModel.uiState.value.loans == listOf(loan1, loan2, loan3))
      assert(manageViewModel.uiState.value.expanded == listOf(false, false, false))
    }
  }
}
