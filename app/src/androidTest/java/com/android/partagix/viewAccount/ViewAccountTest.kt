package com.android.partagix.viewAccount

import android.location.Location
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.partagix.model.InventoryUIState
import com.android.partagix.model.InventoryViewModel
import com.android.partagix.model.category.Category
import com.android.partagix.model.item.Item
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
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

    @Before
    fun testSetup() {
        emptyMockUiState = MutableStateFlow(UserUIState(emptyList(), ""))
        val cat1 = Category("1", "Category 1")
        val vis1 = com.android.partagix.model.visibility.Visibility.PUBLIC
        val loc1 = Location("1")
        val items = listOf(Item("1", cat1, "Name 1", "Description 1", "Author 1", vis1, 1, loc1))
        nonEmptyMockUiState = MutableStateFlow(InventoryUIState(items, ""))

        mockInventoryViewModel = mockk()
        // every { mockInventoryViewModel.uiState } returns emptyMockUiState
        every { mockInventoryViewModel.getInventory() } just Runs
        every { mockInventoryViewModel.filterItems(any()) } just Runs

        mockNavActions = mockk<NavigationActions>()
        every { mockNavActions.navigateTo(Route.HOME) } just Runs
        every { mockNavActions.navigateTo(Route.LOGIN) } just Runs

        /*    composeTestRule.setContent {
          InventoryScreen(mockInventoryViewModel, mockNavActions::navigateTo)
        }*/
    }
}
