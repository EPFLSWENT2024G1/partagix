package com.android.partagix

import android.content.ActivityNotFoundException
import android.content.pm.PackageManager
import android.os.Bundle
import com.android.partagix.model.Database
import com.android.partagix.model.HomeViewModel
import com.android.partagix.model.emptyConst.emptyUser
import com.android.partagix.model.user.User
import io.mockk.Runs
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.junit.Before
import org.junit.Test

class HomeViewModelTests {

  private lateinit var db: Database

  private lateinit var homeViewModel: HomeViewModel

  private lateinit var mockMainActivity: MainActivity

  private val packageName = "com.google.zxing.client.android"

  @Before
  fun setup() {
    db = mockk<Database>()
    mockMainActivity = mockk<MainActivity>()
    homeViewModel = HomeViewModel(db, mockMainActivity)
  }

  @Test
  fun testUpdateUser() {
    every { db.getUser(any(), any(), any()) } answers
        {
          val id = firstArg<String>()
          val onSuccess = thirdArg<(User) -> Unit>()
          onSuccess(emptyUser)
        }

    homeViewModel.updateUser("0")
    assert(homeViewModel.uiState.value.user == emptyUser)
  }

  @Test
  fun openQrScannerInstalled() {
    every { mockMainActivity.packageManager.getLaunchIntentForPackage(any()) } returns mockk()
    every {
      mockMainActivity.packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
    } returns mockk()
    every { mockMainActivity.startActivity(any(), any()) } just Runs

    homeViewModel.openQrScanner()
    coVerify { mockMainActivity.startActivity(any(), any()) }
  }

  @Test
  fun openQrScannerNotInstalled() {
    every { mockMainActivity.packageManager.getLaunchIntentForPackage(any()) } returns mockk()
    every {
      mockMainActivity.packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
    } throws PackageManager.NameNotFoundException()
    every { mockMainActivity.startActivity(any(), any()) } just Runs

    homeViewModel.openQrScanner()
    coVerify { mockMainActivity.startActivity(any(), any()) }
  }

  @Test
  fun testQrScannerNoStore() {
    every { mockMainActivity.packageManager.getLaunchIntentForPackage(any()) } returns mockk()
    every {
      mockMainActivity.packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
    } throws PackageManager.NameNotFoundException()
    every { mockMainActivity.startActivity(any(), any()) } just Runs
    every { mockMainActivity.startActivity(any(), Bundle.EMPTY) } throws ActivityNotFoundException()

    homeViewModel.openQrScanner()
    coVerify { mockMainActivity.startActivity(any(), any()) }
  }
}
