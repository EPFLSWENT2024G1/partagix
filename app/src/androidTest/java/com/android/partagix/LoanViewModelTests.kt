package com.android.partagix

import android.location.Location
import android.util.Log
import com.android.partagix.model.Database
import com.android.partagix.model.LoanViewModel
import com.android.partagix.model.auth.Authentication
import com.android.partagix.model.category.Category
import com.android.partagix.model.item.Item
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.loan.LoanState
import com.android.partagix.model.visibility.Visibility
import com.google.firebase.auth.FirebaseUser
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.spyk
import java.util.Date
import org.junit.Test

class LoanViewModelTests {
  private val item1 =
      Item(
          "1",
          Category("1", "Category 1"),
          "test1",
          "test1",
          Visibility.PUBLIC,
          1,
          Location(""),
          "user1",
      )

  private val item2 =
      Item(
          "2",
          Category("2", "Category 2"),
          "test2",
          "test2",
          Visibility.PUBLIC,
          1,
          Location(""),
          "user2",
      )

  private val item3 =
      Item(
          "3",
          Category("3", "Category 3"),
          "test3",
          "test3",
          Visibility.PRIVATE,
          1,
          Location(""),
          "user3",
      )

  private val item4 =
      Item(
          "4",
          Category("4", "Category 4"),
          "test4",
          "test4",
          Visibility.FRIENDS,
          1,
          Location(""),
          "user4",
      )

  private val item5 =
      Item(
          "5",
          Category("5", "Category 5"),
          "test5",
          "test5",
          Visibility.PUBLIC,
          1,
          Location(""),
          "user5",
      )

  // Single loan, item 2 is loaned to user 3
  private val mockLoans =
      listOf(
          Loan(
              "user2",
              "user3",
              "2",
              Date(2021, 1, 1),
              Date(2025, 1, 2),
              "",
              "",
              "",
              "",
              LoanState.ACCEPTED),
      )

  private val items = listOf(item1, item2, item3, item4, item5)

  @Test
  fun testUpdate() {
    val db = mockk<Database>()
    val loanViewModel = spyk(LoanViewModel(db = db), recordPrivateCalls = false)

    every { db.getLoans(any()) } answers {
      firstArg<(List<Loan>) -> Unit>().invoke(mockLoans)
    }
    every { db.getItems(any()) } answers {
      firstArg<(List<Item>) -> Unit>().invoke(items)
    }

    val update =
        LoanViewModel::class.java.getDeclaredMethod("update", List::class.java, String::class.java)
    update.isAccessible = true

    // update items only
    update.invoke(loanViewModel, items, null)
    assert(loanViewModel.uiState.value.availableItems == items)
    assert(loanViewModel.uiState.value.query == "")

    // update query
    update.invoke(loanViewModel, items, "test")
    assert(loanViewModel.uiState.value.availableItems == items)
    assert(loanViewModel.uiState.value.query == "test")
  }

  @Test
  fun testGetAvailableLoansNoUser() {
    val db = mockk<Database>()
    val loanViewModel = spyk(LoanViewModel(db = db))

    mockkObject(Authentication.Companion)
    every { Authentication.getUser() } returns null

    every { db.getLoans(any()) } answers {
      firstArg<(List<Loan>) -> Unit>().invoke(mockLoans)
    }
    every { db.getItems(any()) } answers {
      firstArg<(List<Item>) -> Unit>().invoke(items)
    }

    loanViewModel.getAvailableLoans()

    // nothing should be updated, may throw an error in the future
    assert(loanViewModel.uiState.value.availableItems.isEmpty())
  }

  @Test
  fun testGetAvailableLoansWithUser1() {
    val db = mockk<Database>()
    val loanViewModel = spyk(LoanViewModel(db = db))

    val mockUser = mockk<FirebaseUser>()

    mockkObject(Authentication.Companion)
    every { Authentication.getUser() } returns mockUser
    every { mockUser.uid } returns "user1"

    every { db.getLoans(any()) } answers {
      firstArg<(List<Loan>) -> Unit>().invoke(mockLoans)
    }
    every { db.getItems(any()) } answers {
      firstArg<(List<Item>) -> Unit>().invoke(items)
    }

    loanViewModel.getAvailableLoans()
    Log.d(TAG, loanViewModel.uiState.value.availableItems.toString())
    assert(loanViewModel.uiState.value.availableItems.size == 1)
    assert(loanViewModel.uiState.value.availableItems.contains(item5))
  }

  @Test
  fun testGetAvailableLoansWithUser5() {
    val db = mockk<Database>()
    val loanViewModel = spyk(LoanViewModel(db = db))

    val mockUser = mockk<FirebaseUser>()

    mockkObject(Authentication.Companion)
    every { Authentication.getUser() } returns mockUser
    every { mockUser.uid } returns "user5"

    every { db.getLoans(any()) } answers {
      firstArg<(List<Loan>) -> Unit>().invoke(mockLoans)
    }
    every { db.getItems(any()) } answers {
      firstArg<(List<Item>) -> Unit>().invoke(items)
    }

    loanViewModel.getAvailableLoans()
    Log.d(TAG, loanViewModel.uiState.value.availableItems.toString())
    assert(loanViewModel.uiState.value.availableItems.size == 1)
    assert(loanViewModel.uiState.value.availableItems.contains(item1))
  }

  companion object {
    private const val TAG = "LoanViewModelTests"
  }
}
