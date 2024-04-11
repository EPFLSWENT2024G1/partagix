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
import com.android.partagix.screens.InventoryScreen
import com.android.partagix.screens.ViewAccount
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route
import com.android.partagix.ui.screens.InventoryScreen
import com.android.partagix.ui.screens.ViewAccount
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
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
class ViewAccountTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

    @get:Rule
    val composeTestRule = createComposeRule()
    @RelaxedMockK
    lateinit var mockNavActions: NavigationActions
    @RelaxedMockK
    lateinit var mockUserViewModel: UserViewModel

    private lateinit var emptyMockUiState: MutableStateFlow<UserUIState>
    private lateinit var nonEmptyMockUiState: MutableStateFlow<UserUIState>

    val cat1 = Category("1", "Category 1")
    val vis1 = com.android.partagix.model.visibility.Visibility.PUBLIC
    val loc1 = Location("1")

    private val emptyUser: User =
        User("", "", "", "", Inventory("", emptyList()))
    private val userOne: User =
        User("id1", "name1", "address1", "rank1", Inventory("id1",
                listOf(Item("1", cat1, "Name 1", "Description 1", "Author 1", vis1, 1, loc1))))

    @Before
    fun testSetup() {
        emptyMockUiState = MutableStateFlow(UserUIState(emptyUser)

        nonEmptyMockUiState = MutableStateFlow(UserUIState(userOne))

        mockUserViewModel = mockk()
        // every { mockInventoryViewModel.uiState } returns emptyMockUiState
//        every { mockUserViewModel.getInventory() } just Runs // no function here

        mockNavActions = mockk<NavigationActions>()
        every { mockNavActions.navigateTo(Route.HOME) } just Runs
        every { mockNavActions.navigateTo(Route.LOGIN) } just Runs
        every { mockNavActions.navigateTo(Route.ACCOUNT) } just Runs
        every { mockNavActions.navigateTo(Route.INVENTORY) } just Runs
        every { mockNavActions.navigateTo(Route.BORROW) } just Runs
        every { mockNavActions.navigateTo(Route.BOOT) } just Runs
        every { mockNavActions.navigateTo(Route.VIEW_ITEM) } just Runs
            // todo right test ? or something for goBack instead ?


        /*    composeTestRule.setContent {
          InventoryScreen(mockInventoryViewModel, mockNavActions::navigateTo)
        }*/
    }

    @Test
    fun testTest() = run {
        every { mockUserViewModel.uiState } returns emptyMockUiState
        composeTestRule.setContent {
            ViewAccount(modifier = Modifier, userViewModel = mockUserViewModel, navigationActions = mockNavActions) // mockNavActions::navigateTo
        }

        assert(true)
    }

    @Test
    fun searchBarIsDisplayed() = run {
        every { mockUserViewModel.uiState } returns emptyMockUiState
        composeTestRule.setContent {
            ViewAccount(modifier = Modifier, userViewModel = mockUserViewModel, navigationActions = mockNavActions) // mockNavActions::navigateTo
        }

        ComposeScreen.onComposeScreen<ViewAccount>(composeTestRule) { searchBar { assertIsDisplayed() } }
    }
}
