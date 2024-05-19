package com.android.partagix.components.locationPicker

import androidx.compose.runtime.MutableState
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.runner.screenshot.Screenshot.capture
import com.android.partagix.model.location.Location
import com.android.partagix.ui.components.locationPicker.LocationPickerViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import java.io.IOException
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocationPickerViewModelTest {
  @get:Rule val composeTestRule = createComposeRule()
  private lateinit var locationState: MutableState<Location?>
  private lateinit var viewModel: LocationPickerViewModel

  @Before
  fun setup() {
    locationState = mockk(relaxed = true)
    viewModel = LocationPickerViewModel()
  }

  @Test
  fun getLocation_SuccessfulResponse_LocationStateUpdated() {
    val responseJson =
        """[{"lat":"51.5073219","lon":"-0.1276474","display_name":"London, England, UK"}]"""
    val mockResponseBody = responseJson.toResponseBody("application/json".toMediaTypeOrNull())
    val mockResponse =
        Response.Builder()
            .code(200)
            .message(responseJson)
            .protocol(Protocol.HTTP_1_1)
            .request(Request.Builder().url("http://test.url").build())
            .body(mockResponseBody)
            .build()

    val mockCall = mockk<Call>()
    coEvery { mockCall.execute() } returns mockResponse

    val mockClient = mockk<OkHttpClient>()
    every { mockClient.newCall(any()) } returns mockCall

    val locationSlot = slot<Location>()

    every { locationState.value = capture(locationSlot) } answers
        {
          locationSlot.captured = locationSlot.captured
        }

    every { locationState.value } answers { locationSlot.captured }

    viewModel.getLocation("London", locationState)
    composeTestRule.waitUntil(10000) { locationSlot.isCaptured }
    val capturedLocation = locationSlot.captured
    assert(capturedLocation.locationName == "London, England United Kingdom")
  }

  @Test
  fun getLocation_FailureResponse_LocationStateNotUpdated() {
    val mockCall = mockk<Call>()
    every { mockCall.execute() } throws IOException("Test exception")

    val mockClient = mockk<OkHttpClient>()
    every { mockClient.newCall(any()) } returns mockCall

    every { locationState.value } returns null

    viewModel.getLocation("InvalidQuery", locationState)

    verify(exactly = 0) { locationState.value = any() }
  }

  @Test
  fun ourLocationToAndroidLocation_NullLocation_ReturnsEmptyAndroidLocation() {
    val location = null
    val androidLocation = viewModel.ourLocationToAndroidLocation(location)
    assert(androidLocation.latitude == 0.0)
    assert(androidLocation.longitude == 0.0)
    assert(androidLocation.extras?.getString("display_name") == null)
  }

  @Test
  fun ourLocationToAndroidLocation_RealLocation_ReturnsEmptyAndroidLocation() {
    val location = Location(1.0, 2.0, "RealPlace")
    val androidLocation = viewModel.ourLocationToAndroidLocation(location)
    assert(androidLocation.latitude == 1.0)
    assert(androidLocation.longitude == 2.0)
    assert(androidLocation.extras?.getString("display_name") == "RealPlace")
  }
}
