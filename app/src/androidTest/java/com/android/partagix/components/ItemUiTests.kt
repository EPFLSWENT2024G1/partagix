package com.android.partagix.components

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.core.content.ContextCompat
import com.android.partagix.model.ManageLoanViewModel
import com.android.partagix.model.category.Category
import com.android.partagix.model.emptyConst.emptyInventory
import com.android.partagix.model.item.Item
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.loan.LoanState
import com.android.partagix.model.user.User
import com.android.partagix.model.visibility.Visibility
import com.android.partagix.ui.components.ClickableText
import com.android.partagix.ui.components.ItemUi
import com.android.partagix.ui.components.isAppInstalled
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.verify
import java.util.Date
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ItemUiTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var viewModel: ManageLoanViewModel
  private lateinit var item: Item
  private lateinit var user: User
  private lateinit var loan: Loan

  @Before
  fun setUp() {
    viewModel = mockk(relaxed = true)

    item =
        Item(
            id = "1",
            name = "Test Item",
            quantity = 1,
            category = Category("1", "Category 1"),
            description = "Test Description",
            location = Location("Test Location"),
            visibility = Visibility.PUBLIC,
        )

    user =
        User(
            id = "8WuTkKJZLTAr6zs5L7rH",
            name = "User1",
            rank = "5",
            email = "test@example.com",
            phoneNumber = "1234567890",
            telegram = "@testuser",
            favorite = listOf(true, true, true),
            address = "Test Address",
            inventory = emptyInventory)

    loan =
        Loan(
            id = "1",
            idItem = "1",
            idBorrower = "2",
            idLender = "1",
            state = LoanState.ACCEPTED,
            startDate = Date(),
            endDate = Date(),
            commentBorrower = "",
            commentLender = "",
            reviewBorrower = "0",
            reviewLender = "0",
        )

    coEvery { viewModel.getUser(any(), any()) } answers { secondArg<(User) -> Unit>().invoke(user) }
  }

  @Test
  fun testItemUiExpanded() {
    composeTestRule.setContent {
      ItemUi(
          item = item,
          user = user,
          loan = loan,
          manageLoanViewModel = viewModel,
          isExpandable = true,
          expandState = false,
          navigationActions = mockk(),
      )
    }

    composeTestRule.onNodeWithText("Test Item").assertIsDisplayed().performClick()

    // Verify preferred contact is displayed
    composeTestRule.onNodeWithText("Preferred contact: ").assertIsDisplayed()
    composeTestRule.onNodeWithText("Email : test@example.com").assertIsDisplayed()
    composeTestRule.onNodeWithText("Phone : 1234567890").assertIsDisplayed()
    composeTestRule.onNodeWithText("Telegram : @testuser").assertIsDisplayed()
  }

  @Test
  fun testAppInstalled() {
    // Mocking the context and package manager
    val mockContext = mockk<Context>()
    val mockPackageManager = mockk<PackageManager>()

    // Stubbing package manager behavior
    every { mockContext.packageManager } returns mockPackageManager

    // Simulate the app being installed
    every {
      mockPackageManager.getPackageInfo("com.example.testapp", PackageManager.GET_ACTIVITIES)
    } returns mockk()

    // Call the function under test
    val result = isAppInstalled(mockContext, "com.example.testapp")

    // Assert that the function returns true when the app is installed
    assertTrue(result)
  }

  @Test
  fun testAppNotInstalled() {
    // Mocking the context and package manager
    val mockContext = mockk<Context>()
    val mockPackageManager = mockk<PackageManager>()

    // Stubbing package manager behavior
    every { mockContext.packageManager } returns mockPackageManager

    // Simulate the app not being installed
    every {
      mockPackageManager.getPackageInfo("com.example.testapp", PackageManager.GET_ACTIVITIES)
    } throws PackageManager.NameNotFoundException()

    // Call the function under test
    val result = isAppInstalled(mockContext, "com.example.testapp")

    // Assert that the function returns false when the app is not installed
    assertFalse(result)
  }

  @Test
  fun testClickableTextEmail() {
    val intent = mockk<Intent>()

    every { intent.resolveActivity(any()) } returns null

    // Mock behavior for ContextCompat.startActivity
    mockkStatic(ContextCompat::class)
    val slotIntent = slot<Intent>()
    every { ContextCompat.startActivity(any(), capture(slotIntent), any()) } returns Unit

    composeTestRule.setContent { ClickableText("Email : example@example.com") }

    // Verify that clicking on the text launches the correct intent
    composeTestRule.onNodeWithText("Email : example@example.com").assertExists().performClick()

    // Extract the captured intent
    val launchedIntent = slotIntent.captured

    // Verify that startActivity was called with the correct intent data
    val expectedIntentEmail = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:example@example.com"))
    assertEquals(expectedIntentEmail.data, launchedIntent.data)
    assertEquals(expectedIntentEmail.action, launchedIntent.action)
    // You can add more assertions if needed

    // Verify that startActivity was called
    verify { ContextCompat.startActivity(any(), any(), any()) }
  }

  @Test
  fun testClickableTextPhone() {
    val intent = mockk<Intent>()

    every { intent.resolveActivity(any()) } returns null

    // Mock behavior for ContextCompat.startActivity
    mockkStatic(ContextCompat::class)
    val slotIntent = slot<Intent>()
    every { ContextCompat.startActivity(any(), capture(slotIntent), any()) } returns Unit

    // Test for Phone number
    composeTestRule.setContent { ClickableText("Phone : +1234567890") }

    // Verify that clicking on the text launches the correct intent for phone number
    composeTestRule.onNodeWithText("Phone : +1234567890").assertExists().performClick()

    // Extract the captured intent
    val launchedIntent = slotIntent.captured

    // Verify that startActivity was called with the correct intent data for phone number
    val expectedPhoneIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:+1234567890"))
    assertEquals(expectedPhoneIntent.data, launchedIntent.data)
    assertEquals(expectedPhoneIntent.action, launchedIntent.action)

    // Verify that startActivity was called
    verify { ContextCompat.startActivity(any(), any(), any()) }
  }

  @Test
  fun testClickableTextTelegram() {
    val intent = mockk<Intent>()

    every { intent.resolveActivity(any()) } returns null

    // Mock behavior for ContextCompat.startActivity
    mockkStatic(ContextCompat::class)
    val slotIntent = slot<Intent>()
    every { ContextCompat.startActivity(any(), capture(slotIntent), any()) } returns Unit

    // Test for Telegram
    composeTestRule.setContent { ClickableText("Telegram : @example") }

    // Verify that clicking on the text launches the correct intent for Telegram
    composeTestRule.onNodeWithText("Telegram : @example").assertExists().performClick()

    // Extract the captured intent
    val launchedIntent = slotIntent.captured

    // Verify that startActivity was called with the correct intent data for Telegram
    val expectedTelegramWebUri = Uri.parse("https://t.me/example")
    val expectedTelegramIntent = Intent(Intent.ACTION_VIEW, expectedTelegramWebUri)
    assertEquals(expectedTelegramIntent.data, launchedIntent.data)
    assertEquals(expectedTelegramIntent.action, launchedIntent.action)

    // Verify that startActivity was called
    verify { ContextCompat.startActivity(any(), any(), any()) }
  }
}
