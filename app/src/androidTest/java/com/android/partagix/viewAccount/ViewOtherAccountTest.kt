package com.android.partagix.viewAccount

import android.location.Location
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.partagix.model.UserUIState
import com.android.partagix.model.UserViewModel
import com.android.partagix.model.category.Category
import com.android.partagix.model.inventory.Inventory
import com.android.partagix.model.item.Item
import com.android.partagix.model.user.User
import com.android.partagix.screens.ViewOtherAccount
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route
import com.android.partagix.ui.screens.ViewOtherAccount
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ViewOtherAccountTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule val composeTestRule = createComposeRule()

  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  @RelaxedMockK lateinit var mockUserViewModel: UserViewModel
  @RelaxedMockK lateinit var mockOtherUserViewModel: UserViewModel

  private lateinit var emptyMockUiState: MutableStateFlow<UserUIState>
  private lateinit var nonEmptyMockUiState: MutableStateFlow<UserUIState>
  private lateinit var otherUserMockUIState: MutableStateFlow<UserUIState>

  private val cat1 = Category("1", "Category 1")
  private val vis1 = com.android.partagix.model.visibility.Visibility.PUBLIC
  private val loc1 = Location("1")

  private val emptyUser: User = User("", "", "", "", Inventory("", emptyList()))
  private val userOne: User =
      User(
          "id1",
          "name1",
          "address1",
          "1",
          Inventory("id1", listOf(Item("1", cat1, "Name 1", "Description 1", vis1, 1, loc1))))

  private val otherUser: User =
      User(
          "id2",
          "name2",
          "address2",
          "2",
          Inventory("id2", listOf(Item("2", cat1, "Name 2", "Description 2", vis1, 2, loc1))))

  private val comments =
      listOf(
          Pair(otherUser, "comment1"),
      )

  @Before
  fun testSetup() {
    emptyMockUiState = MutableStateFlow(UserUIState(emptyUser))
    nonEmptyMockUiState = MutableStateFlow(UserUIState(userOne, comments = comments))
    otherUserMockUIState = MutableStateFlow(UserUIState(userOne))

    mockUserViewModel = mockk()
    mockOtherUserViewModel = mockk()

    every { mockOtherUserViewModel.uiState } returns otherUserMockUIState

    mockNavActions = mockk<NavigationActions>()
    every { mockNavActions.navigateTo(Route.HOME) } just Runs
    every { mockNavActions.navigateTo(Route.ACCOUNT) } just Runs
    every { mockNavActions.navigateTo(Route.INVENTORY) } just Runs
    every { mockNavActions.navigateTo(Route.LOAN) } just Runs
    every { mockNavActions.navigateTo(Route.EDIT_ACCOUNT) } just Runs
    every { mockNavActions.goBack() } just Runs
  }

  @Test
  fun contentIsDisplayed() = run {
    every { mockUserViewModel.uiState } returns emptyMockUiState
    composeTestRule.setContent {
      ViewOtherAccount(
          modifier = Modifier,
          navigationActions = mockNavActions,
          userViewModel = mockUserViewModel,
          otherUserViewModel = mockOtherUserViewModel)
    }

    onComposeScreen<ViewOtherAccount>(composeTestRule) {
      topBar { assertIsDisplayed() }
      accountScreenBottomNavBar { assertIsDisplayed() }
      mainContent { assertIsDisplayed() }
      userImage { assertIsDisplayed() }
      username { assertIsDisplayed() }
      address { assertIsDisplayed() }
      rating { assertIsDisplayed() }
    }
  }

  @Test
  fun componentsWorks() = run {
    every { mockUserViewModel.uiState } returns emptyMockUiState
    composeTestRule.setContent {
      ViewOtherAccount(
          modifier = Modifier,
          navigationActions = mockNavActions,
          userViewModel = mockUserViewModel,
          otherUserViewModel = mockOtherUserViewModel)
    }

    onComposeScreen<ViewOtherAccount>(composeTestRule) {
      // topBar
      topBar { assertIsDisplayed() }
      title { assertIsDisplayed() }
      backButton { assertIsDisplayed() }
      backButton { performClick() }

      // username
      username { assertIsDisplayed() }
      usernameText { assertIsDisplayed() }
      val username = mockOtherUserViewModel.uiState.value.user.name
      usernameText { assertTextEquals("$username's profile") }

      //    editButton
      @Test
      fun editAndFriendButtonIsDisplayed() = run {
        every { mockUserViewModel.uiState } returns emptyMockUiState
        composeTestRule.setContent {
          ViewOtherAccount(
              modifier = Modifier,
              navigationActions = mockNavActions,
              userViewModel = mockUserViewModel,
              otherUserViewModel = mockOtherUserViewModel)
        }

        onComposeScreen<ViewOtherAccount>(composeTestRule) { editButton { assertIsDisplayed() } }
      }

      @Test
      fun editAndFriendButtonWorks() = run {
        every { mockUserViewModel.uiState } returns emptyMockUiState
        composeTestRule.setContent {
          ViewOtherAccount(
              modifier = Modifier,
              navigationActions = mockNavActions,
              userViewModel = mockUserViewModel,
              otherUserViewModel = mockOtherUserViewModel)
        }

        onComposeScreen<ViewOtherAccount>(composeTestRule) {
          editButton { assertIsDisplayed() }
          editButton { performClick() }
        }
      }
    }
  }
}
