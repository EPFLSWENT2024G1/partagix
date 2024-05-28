package com.android.partagix.editAccount

import android.location.Location
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.partagix.model.UserUIState
import com.android.partagix.model.UserViewModel
import com.android.partagix.model.category.Category
import com.android.partagix.model.inventory.Inventory
import com.android.partagix.model.item.Item
import com.android.partagix.model.user.User
import com.android.partagix.screens.EditAccount
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.screens.EditAccount
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EditAccountTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  @RelaxedMockK lateinit var mockNavActions: NavigationActions
  @RelaxedMockK lateinit var mockUserViewModel: UserViewModel

  private lateinit var nonEmptyMockUiState: MutableStateFlow<UserUIState>

  val cat1 = Category("1", "Category 1")
  val vis1 = com.android.partagix.model.visibility.Visibility.PUBLIC
  val loc1 = Location("1")

  private val userOne: User =
      User(
          "id1",
          "name1",
          "address1",
          "rank1",
          email = "email@test.com",
          inventory =
              Inventory("id1", listOf(Item("1", cat1, "Name 1", "Description 1", vis1, 1, loc1))))

  @Before
  fun testSetup() {
    nonEmptyMockUiState = MutableStateFlow(UserUIState(userOne))

    mockUserViewModel = mockk()
    every { mockUserViewModel.updateUser(any()) } just Runs

    mockNavActions = mockk<NavigationActions>()
    every { mockNavActions.goBack() } just Runs
  }

  /** Test if the EditAccount screen is displayed correctly */
  @Test
  fun isDisplayedGoodUser() {
    every { mockUserViewModel.uiState } returns nonEmptyMockUiState
    every { mockUserViewModel.getLoggedUserId() } returns "id1"
    composeTestRule.setContent {
      EditAccount(
          modifier = Modifier,
          userViewModel = mockUserViewModel,
          navigationActions = mockNavActions,
          locationViewModel = mockk())
    }

    ComposeScreen.onComposeScreen<EditAccount>(composeTestRule) {
      topBar { assertIsDisplayed() }
      backButton { assertIsDisplayed() }
      bottomNavBar { assertIsDisplayed() }
      mainContent { assertIsDisplayed() }
      composeTestRule.waitUntil(10000) { composeTestRule.onNodeWithTag("image").isDisplayed() }
      usernameField { assertIsDisplayed() }
      addressField { assertIsDisplayed() }
      actionButtons { assertIsDisplayed() }
      saveButton { assertIsDisplayed() }
      email { assertIsDisplayed() }
      phoneNumber { assertIsDisplayed() }
      telegram { assertIsDisplayed() }
      contactInfo { assertIsDisplayed() }
      emailCheckBox {
        assertIsDisplayed()
        assertHasClickAction()
      }
      phoneNumberCheckBox {
        assertIsDisplayed()
        assertHasClickAction()
      }
      telegramCheckBox {
        assertIsDisplayed()
        assertHasClickAction()
      }
    }
  }

  // * Test if the EditAccount screen doesn't allow editing other user profiles */
  @Test
  fun isDisplayedBadUser() {
    every { mockUserViewModel.uiState } returns nonEmptyMockUiState
    every { mockUserViewModel.getLoggedUserId() } returns "not_id1"
    composeTestRule.setContent {
      EditAccount(
          modifier = Modifier,
          userViewModel = mockUserViewModel,
          navigationActions = mockNavActions,
          locationViewModel = mockk())
    }

    ComposeScreen.onComposeScreen<EditAccount>(composeTestRule) {
      topBar { assertIsDisplayed() }
      backButton { assertIsDisplayed() }
      bottomNavBar { assertIsDisplayed() }
      notYourAccount { assertIsDisplayed() }
    }
  }

  /** Test if the 'back' button works correctly */
  @Test
  fun backButtonClick() {
    every { mockUserViewModel.uiState } returns nonEmptyMockUiState
    every { mockUserViewModel.getLoggedUserId() } returns "id1"
    composeTestRule.setContent {
      EditAccount(
          modifier = Modifier,
          userViewModel = mockUserViewModel,
          navigationActions = mockNavActions,
          locationViewModel = mockk())
    }

    ComposeScreen.onComposeScreen<EditAccount>(composeTestRule) { backButton { performClick() } }

    verify(exactly = 1) { mockNavActions.goBack() }
  }

  /** Test if the 'save' button works correctly */
  @Test
  fun saveButtonClick() {
    every { mockUserViewModel.uiState } returns nonEmptyMockUiState
    every { mockUserViewModel.getLoggedUserId() } returns "id1"
    composeTestRule.setContent {
      EditAccount(
          modifier = Modifier,
          userViewModel = mockUserViewModel,
          navigationActions = mockNavActions,
          locationViewModel = mockk())
    }

    ComposeScreen.onComposeScreen<EditAccount>(composeTestRule) {
      saveButton {
        performScrollTo()
        performClick()
      }
    }

    verify(exactly = 1) { mockUserViewModel.updateUser(any()) }
    verify(exactly = 1) { mockNavActions.goBack() }
  }
}
