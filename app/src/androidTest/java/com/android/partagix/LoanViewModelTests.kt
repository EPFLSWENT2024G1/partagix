package com.android.partagix

import android.location.Location
import android.util.Log
import com.android.partagix.model.Database
import com.android.partagix.model.FilterState
import com.android.partagix.model.LoanViewModel
import com.android.partagix.model.auth.Authentication
import com.android.partagix.model.category.Category
import com.android.partagix.model.filtering.Filtering
import com.android.partagix.model.item.Item
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.loan.LoanState
import com.android.partagix.model.user.User
import com.android.partagix.model.visibility.Visibility
import com.google.firebase.auth.FirebaseUser
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.spyk
import io.mockk.verify
import java.util.Date
import java.util.concurrent.CountDownLatch
import junit.framework.TestCase.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test

class LoanViewModelTests {
  private val db = mockk<Database>()
  private val loanViewModel = spyk(LoanViewModel(db = db))

  private val currentPosition =
      Location("").apply {
        latitude = 46.5294513
        longitude = 6.5864534
      }

  private val item1 =
      Item(
          "1",
          Category("1", "Category 1"),
          "test1",
          "test1",
          Visibility.PUBLIC,
          1,
          Location("").apply {
            // Renens Aqua Park
            latitude = 46.5297788
            longitude = 6.5831585
          },
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
          Location("").apply {
            // Renens
            latitude = 46.534633
            longitude = 6.588432
          },
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
          Visibility.PUBLIC,
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

  val user1 = User("user1", "U1", "addr1", "0", mockk())

  val user2 = User("user2", "U2", "addr2", "0", mockk())

  val user3 = User("user3", "U3", "addr3", "0", mockk())

  val user4 = User("user4", "U4", "addr4", "0", mockk())

  val user5 = User("user5", "U5", "addr5", "0", mockk())

  val users_list = listOf(user1, user2, user3, user4, user5)

  // Single loan, item 2 is loaned to user 3
  private val mockLoans =
      listOf(
          Loan(
              "fmdsaon",
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
  private val users = listOf(user1, user2, user3, user4, user5)

  @Before
  fun setUp() {
    clearAllMocks()

    every { db.getLoans(any()) } answers { firstArg<(List<Loan>) -> Unit>().invoke(mockLoans) }
    every { db.getItems(any()) } answers { firstArg<(List<Item>) -> Unit>().invoke(items) }
    every { db.getItemsWithImages(any()) } answers
        {
          firstArg<(List<Item>) -> Unit>().invoke(items)
        }

    every { db.getUser(any(), any(), any()) } answers
        {
          val userId = firstArg<String>()
          thirdArg<(User) -> Unit>().invoke(users.first { it.id == userId })
        }
    every { db.getItemUnavailability(any(), any()) } answers
        {
          secondArg<(List<Date>) -> Unit>()
              .invoke(
                  listOf(
                      Date(2000, 1, 1),
                      Date(2001, 1, 1),
                      Date(2002, 1, 1),
                      Date(2003, 1, 1),
                      Date()))
        }
  }

  @After
  fun tearDown() {
    clearAllMocks()
  }

  @Test
  fun testUpdate() {
    val update =
        LoanViewModel::class
            .java
            .getDeclaredMethod("update", List::class.java, FilterState::class.java)
    update.isAccessible = true

    // update items only
    update.invoke(loanViewModel, items, null)
    assert(loanViewModel.uiState.value.availableLoans == items)
    assert(loanViewModel.uiState.value.filterState.query == null)

    // update query
    update.invoke(loanViewModel, items, FilterState(query = "test"))
    assert(loanViewModel.uiState.value.availableLoans == items)
    assert(loanViewModel.uiState.value.filterState.query == "test")
  }

  @Test
  fun testGetAvailableLoansNoUser() {
    mockkObject(Authentication.Companion)
    every { Authentication.getUser() } returns null

    // nothing should be updated, may throw an error in the future
    assert(loanViewModel.uiState.value.availableLoans.isEmpty())
  }

  @Test
  fun testGetAvailableLoansWithUser1() {
    val mockUser = mockk<FirebaseUser>()

    mockkObject(Authentication.Companion)
    every { Authentication.getUser() } returns mockUser
    every { mockUser.uid } returns "user1"

    val availableItems = listOf(item5)

    every { db.getAvailableItems(any(), any()) } answers
        {
          secondArg<(List<Item>) -> Unit>().invoke(availableItems)
        }
    every { db.getUsers(any()) } answers { firstArg<(List<User>) -> Unit>().invoke(users_list) }

    val latch = CountDownLatch(1)
    loanViewModel.getAvailableLoans(latch = latch)
    latch.await()

    verify { db.getAvailableItems(any(), any()) }

    assert(loanViewModel.uiState.value.availableLoans.size == 1)
    assert(loanViewModel.uiState.value.availableLoans.any { it.item == item5 })
  }

  @Test
  fun testGetAvailableLoansWithUser5() {

    val availableItems = listOf(item1)

    every { db.getAvailableItems(any(), any()) } answers
        {
          secondArg<(List<Item>) -> Unit>().invoke(availableItems)
        }
    every { db.getUsers(any()) } answers { firstArg<(List<User>) -> Unit>().invoke(users_list) }

    val mockUser = mockk<FirebaseUser>()

    mockkObject(Authentication.Companion)
    every { Authentication.getUser() } returns mockUser
    every { mockUser.uid } returns "user5"
    val latch = CountDownLatch(1)
    loanViewModel.getAvailableLoans(latch = latch)
    latch.await()

    verify { db.getAvailableItems(any(), any()) }

    Log.d(TAG, loanViewModel.uiState.value.availableLoans.toString())
    assert(loanViewModel.uiState.value.availableLoans.size == 1)
    assert(loanViewModel.uiState.value.availableLoans.any { it.item == item1 })
  }

  // Verify that the filterItems method calls the filtering method
  @Test
  fun testFilterItemsByQuery() {
    val spyFiltering = spyk(Filtering())
    val loanViewModel = spyk(LoanViewModel(filtering = spyFiltering, db = db))

    // all items
    val query = "test"
    loanViewModel.applyFilters(FilterState(query = query))

    verify { spyFiltering.filterItems(any(), query) }
  }

  // Verify that the filterItems method calls the filtering method
  @Test
  fun testFilterItemsByAtLeastQuantity() {
    val spyFiltering = spyk(Filtering())
    val loanViewModel = spyk(LoanViewModel(filtering = spyFiltering, db = db))

    // all items
    val atLeastQuantity = 1
    loanViewModel.applyFilters(FilterState(atLeastQuantity = atLeastQuantity))

    verify { spyFiltering.filterItems(any(), atLeastQuantity) }
  }

  // Verify that the filterItems method calls the filtering method
  @Test
  fun testFilterItemsByCurrentPosition() {
    val spyFiltering = spyk(Filtering())
    val loanViewModel = spyk(LoanViewModel(filtering = spyFiltering, db = db))

    // all items
    val radius = 1.0
    loanViewModel.applyFilters(FilterState(location = currentPosition, radius = radius))

    verify { spyFiltering.filterItems(any(), currentPosition, radius) }
  }

  @Test
  fun testAvailability() {
    val mockUser = mockk<FirebaseUser>()
    mockkObject(Authentication.Companion)
    every { Authentication.getUser() } returns mockUser
    every { mockUser.uid } returns "user5"

    val availableItems = listOf(item5)

    every { db.getAvailableItems(any(), any()) } answers
        {
          secondArg<(List<Item>) -> Unit>().invoke(availableItems)
        }
    every { db.getUsers(any()) } answers { firstArg<(List<User>) -> Unit>().invoke(users_list) }

    val latch = CountDownLatch(1)
    loanViewModel.getAvailableLoans(latch)
    latch.await()
    assertEquals(loanViewModel.uiState.value.availability.size, 1)
    assertEquals(loanViewModel.uiState.value.availability, listOf(false))
  }

  companion object {
    private const val TAG = "LoanViewModelTests"
  }
}
