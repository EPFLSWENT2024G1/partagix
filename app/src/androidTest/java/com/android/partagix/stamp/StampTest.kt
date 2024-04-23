package com.android.partagix.stamp

import android.location.Location
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.partagix.model.StampViewModel
import com.android.partagix.model.UserViewModel
import com.android.partagix.model.category.Category
import com.android.partagix.model.inventory.Inventory
import com.android.partagix.model.item.Item
import com.android.partagix.model.user.User
import com.android.partagix.screens.StampScreen
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route
import com.android.partagix.ui.screens.Stamp
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
class StampTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()
  @RelaxedMockK lateinit var mockNavActions: NavigationActions
  @RelaxedMockK lateinit var mockStampViewModel: StampViewModel

  private lateinit var nonEmptyMockUiState: MutableStateFlow<UserUIState>

// stampScreen
  @Test
  fun accountScreenBottomNavBarIsDisplayed() = run {
    every { mockStampViewModel } returns mockStampViewModel
    composeTestRule.setContent {
      Stamp(
        modifier = Modifier,
        stampViewModel = mockStampViewModel,
        navigationActions = mockNavActions) // mockNavActions::navigateTo
    }

    onComposeScreen<StampScreen>(composeTestRule) {
      accountScreenBottomNavBar { assertIsDisplayed() }
    }
  }

}