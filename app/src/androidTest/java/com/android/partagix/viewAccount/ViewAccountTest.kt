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
import com.android.partagix.screens.ViewAccount
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route
import com.android.partagix.ui.screens.ViewAccount
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.mockk
import kotlin.math.round
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ViewAccountTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule val composeTestRule = createComposeRule()

  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  @RelaxedMockK lateinit var mockUserViewModel: UserViewModel

  private lateinit var emptyMockUiState: MutableStateFlow<UserUIState>
  private lateinit var nonEmptyMockUiState: MutableStateFlow<UserUIState>

  val cat1 = Category("1", "Category 1")
  val vis1 = com.android.partagix.model.visibility.Visibility.PUBLIC
  val loc1 = Location("1")

  private val emptyUser: User =
    User("", "", "", "", Inventory("", emptyList()))
  private val userOne: User =
      User(
          "id1",
          "name1",
          "address1",
          "rank1",
          Inventory("id1", listOf(
            Item("1", cat1, "Name 1", "Description 1", vis1, 1, loc1))))

  @Before
  fun testSetup() {
    emptyMockUiState = MutableStateFlow(UserUIState(emptyUser))
    nonEmptyMockUiState = MutableStateFlow(UserUIState(userOne))

    mockUserViewModel = mockk()
    mockNavActions = mockk<NavigationActions>()
    every { mockNavActions.navigateTo(Route.HOME) } just Runs
    every { mockNavActions.navigateTo(Route.ACCOUNT) } just Runs
    every { mockNavActions.navigateTo(Route.INVENTORY) } just Runs
    every { mockNavActions.navigateTo(Route.LOAN) } just Runs
    every { mockNavActions.goBack() } just Runs
  }

  @Test
  fun testTest() = run {
    every { mockUserViewModel.uiState } returns emptyMockUiState
    composeTestRule.setContent {
      ViewAccount(
          modifier = Modifier,
          userViewModel = mockUserViewModel,
          navigationActions = mockNavActions)
    }

    assert(true)
  }

  //    topBar
  @Test
  fun topBarIsDisplayed() = run {
    every { mockUserViewModel.uiState } returns emptyMockUiState
    composeTestRule.setContent {
      ViewAccount(
          modifier = Modifier,
          userViewModel = mockUserViewModel,
          navigationActions = mockNavActions)
    }

    onComposeScreen<ViewAccount>(composeTestRule) { topBar { assertIsDisplayed() } }
  }

  @Test
  fun topBarWorks() = run {
    every { mockUserViewModel.uiState } returns emptyMockUiState
    composeTestRule.setContent {
      ViewAccount(
          modifier = Modifier,
          userViewModel = mockUserViewModel,
          navigationActions = mockNavActions)
    }

    onComposeScreen<ViewAccount>(composeTestRule) {
      topBar { assertIsDisplayed() }
      title { assertIsDisplayed() }
      backButton { assertIsDisplayed() }
      backButton { performClick() }
    }
  }

  //    accountScreenBottomNavBar
  @Test
  fun accountScreenBottomNavBarIsDisplayed() = run {
    every { mockUserViewModel.uiState } returns emptyMockUiState
    composeTestRule.setContent {
      ViewAccount(
          modifier = Modifier,
          userViewModel = mockUserViewModel,
          navigationActions = mockNavActions)
    }

    onComposeScreen<ViewAccount>(composeTestRule) {
      accountScreenBottomNavBar { assertIsDisplayed() }
    }
  }

  //    mainContent
  @Test
  fun mainContentIsDisplayed() = run {
    every { mockUserViewModel.uiState } returns emptyMockUiState
    composeTestRule.setContent {
      ViewAccount(
          modifier = Modifier,
          userViewModel = mockUserViewModel,
          navigationActions = mockNavActions)
    }

    onComposeScreen<ViewAccount>(composeTestRule) { mainContent { assertIsDisplayed() } }
  }

  //    userImage
  @Test
  fun userImageIsDisplayed() = run {
    every { mockUserViewModel.uiState } returns emptyMockUiState
    composeTestRule.setContent {
      ViewAccount(
          modifier = Modifier,
          userViewModel = mockUserViewModel,
          navigationActions = mockNavActions)
    }

    onComposeScreen<ViewAccount>(composeTestRule) { userImage { assertIsDisplayed() } }
  }

  //    username
  @Test
  fun usernameIsDisplayed() = run {
    every { mockUserViewModel.uiState } returns emptyMockUiState
    composeTestRule.setContent {
      ViewAccount(
          modifier = Modifier,
          userViewModel = mockUserViewModel,
          navigationActions = mockNavActions)
    }

    onComposeScreen<ViewAccount>(composeTestRule) { username { assertIsDisplayed() } }
  }

  // usernameText
  @Test
  fun usernameWorks() = run {
    every { mockUserViewModel.uiState } returns emptyMockUiState
    composeTestRule.setContent {
      ViewAccount(
          modifier = Modifier,
          userViewModel = mockUserViewModel,
          navigationActions = mockNavActions)
    }

    onComposeScreen<ViewAccount>(composeTestRule) {
      username { assertIsDisplayed() }
      usernameText { assertIsDisplayed() }
      val username = mockUserViewModel.uiState.value.user.name
      usernameText { assertTextEquals("$username's profile") }
    }
  }

  //    address
  @Test
  fun addressIsDisplayed() = run {
    every { mockUserViewModel.uiState } returns emptyMockUiState
    composeTestRule.setContent {
      ViewAccount(
          modifier = Modifier,
          userViewModel = mockUserViewModel,
          navigationActions = mockNavActions)
    }

    onComposeScreen<ViewAccount>(composeTestRule) { address { assertIsDisplayed() } }
  }

  @Test
  fun addressWorks() = run {
    every { mockUserViewModel.uiState } returns emptyMockUiState
    composeTestRule.setContent {
      ViewAccount(
          modifier = Modifier,
          userViewModel = mockUserViewModel,
          navigationActions = mockNavActions)
    }

    onComposeScreen<ViewAccount>(composeTestRule) {
      address { assertIsDisplayed() }
      val userAddress = mockUserViewModel.uiState.value.user.address
      //        address { assertValueEquals(userAddress)}
      //        addressText { assertIsDisplayed() }
      //        addressIcon { assertIsDisplayed() }
    }
  }

  //    rating
  @Test
  fun ratingIsDisplayed() = run {
    every { mockUserViewModel.uiState } returns emptyMockUiState
    composeTestRule.setContent {
      ViewAccount(
          modifier = Modifier,
          userViewModel = mockUserViewModel,
          navigationActions = mockNavActions)
    }

    onComposeScreen<ViewAccount>(composeTestRule) { rating { assertIsDisplayed() } }
  }

  @Test
  fun ratingWorks() = run {
    every { mockUserViewModel.uiState } returns emptyMockUiState
    composeTestRule.setContent {
      ViewAccount(
          modifier = Modifier,
          userViewModel = mockUserViewModel,
          navigationActions = mockNavActions)
    }

    onComposeScreen<ViewAccount>(composeTestRule) {
      rating { assertIsDisplayed() }
      //        ratingText { assertIsDisplayed() }

      val rank = mockUserViewModel.uiState.value.user.rank
      if (rank == "") {
        //            ratingText { assertTextEquals("No trust yet") }
      } else {
        ratingText { assertTextContains("/5)") }

        val ratingUnit = round(rank.toFloat())
        when (ratingUnit.toInt()) {
          0 -> {
            ratingText { assertTextContains("☆☆☆☆☆") }
          }
          1 -> {
            ratingText { assertTextContains("★☆☆☆☆") }
          }
          2 -> {
            ratingText { assertTextContains("★★☆☆☆") }
          }
          3 -> {
            ratingText { assertTextContains("★★★☆☆") }
          }
          4 -> {
            ratingText { assertTextContains("★★★★☆") }
          }
          5 -> {
            ratingText { assertTextContains("★★★★★") }
          }
          else -> {
            ratingText { assertTextContains("...") }
          }
        }
      }
    }
  }

  //    actionButtons
  @Test
  fun actionButtonsIsDisplayed() = run {
    every { mockUserViewModel.uiState } returns emptyMockUiState
    composeTestRule.setContent {
      ViewAccount(
          modifier = Modifier,
          userViewModel = mockUserViewModel,
          navigationActions = mockNavActions)
    }

    onComposeScreen<ViewAccount>(composeTestRule) { actionButtons { assertIsDisplayed() } }
  }

  //    inventoryButton
  @Test
  fun inventoryButtonIsDisplayed() = run {
    every { mockUserViewModel.uiState } returns emptyMockUiState
    composeTestRule.setContent {
      ViewAccount(
          modifier = Modifier,
          userViewModel = mockUserViewModel,
          navigationActions = mockNavActions)
    }

    onComposeScreen<ViewAccount>(composeTestRule) { inventoryButton { assertIsDisplayed() } }
  }

  @Test
  fun inventoryButtonWorks() = run {
    every { mockUserViewModel.uiState } returns emptyMockUiState
    composeTestRule.setContent {
      ViewAccount(
          modifier = Modifier,
          userViewModel = mockUserViewModel,
          navigationActions = mockNavActions)
    }

    onComposeScreen<ViewAccount>(composeTestRule) {
      inventoryButton { assertIsDisplayed() }
      inventoryButton { performClick() }
      //        inventoryButtonText { assertIsDisplayed() }
    }
  }

  //    friendButton
  @Test
  fun friendButtonIsDisplayed() = run {
    every { mockUserViewModel.uiState } returns emptyMockUiState
    composeTestRule.setContent {
      ViewAccount(
          modifier = Modifier,
          userViewModel = mockUserViewModel,
          navigationActions = mockNavActions)
    }

    onComposeScreen<ViewAccount>(composeTestRule) { friendButton { assertIsDisplayed() } }
  }

  @Test
  fun friendButtonWorks() = run {
    every { mockUserViewModel.uiState } returns emptyMockUiState
    composeTestRule.setContent {
      ViewAccount(
          modifier = Modifier,
          userViewModel = mockUserViewModel,
          navigationActions = mockNavActions)
    }

    onComposeScreen<ViewAccount>(composeTestRule) {
      friendButton { assertIsDisplayed() }
      friendButton { performClick() }
      //        viewAccount { assertIsNotFocused() }
    }
  }
}
