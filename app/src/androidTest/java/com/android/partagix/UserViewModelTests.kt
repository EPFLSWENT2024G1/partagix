package com.android.partagix

import android.net.Uri
import com.android.partagix.model.Database
import com.android.partagix.model.StorageV2
import com.android.partagix.model.UserViewModel
import com.android.partagix.model.auth.Authentication
import com.android.partagix.model.emptyConst.emptyUser
import com.android.partagix.model.user.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.storage
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.mockkObject
import java.io.File
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test

class UserViewModelTests {
  @RelaxedMockK lateinit var userViewModel: UserViewModel
  @RelaxedMockK lateinit var mockDatabase: Database
  @RelaxedMockK lateinit var mockStorage: StorageV2

  @RelaxedMockK lateinit var firebaseUser: FirebaseUser

  private val currUser = emptyUser.copy("id1", "name1")
  private val otherUser = emptyUser.copy("id2", "name2")

  private val comment = "comment"

  @Before
  fun setUp() {
    mockDatabase = mockk()

    every { mockDatabase.getUserWithImage(any(), any(), any()) } answers
        {
          val callback: (User) -> Unit = thirdArg()
          callback(otherUser)
        }

    every { mockDatabase.getComments(any(), any()) } answers
        {
          val callback: (List<Pair<User, String>>) -> Unit = secondArg()
          callback(listOf(Pair(otherUser, comment)))
        }

    firebaseUser = mockk()

    mockkObject(Authentication)
    every { Authentication.getUser() } returns firebaseUser
    every { firebaseUser.uid } returns "id1"

    mockStorage = mockk<StorageV2>()
    every { mockStorage.uploadImageToFirebaseStorage(any(), any(), any(), any()) } answers
        {
          val callback = args[3] as () -> Unit
          callback()
        }
    val tempFile = File("tempFile.tmp")
    every { mockStorage.getImageFromFirebaseStorage(any(), any(), any(), any()) } answers
        {
          val callback = args[3] as (File) -> Unit
          callback(tempFile)
        }
    userViewModel = UserViewModel(user = currUser, db = mockDatabase, imageStorage = mockStorage)
  }

  @Test
  fun getCommentsWorks() {
    userViewModel.getComments()

    val comments = userViewModel.uiState.value.comments
    assert(comments.first().first == otherUser)
    assert(comments.first().second == comment)
  }

  @Test
  fun setLoadingWorks() {
    userViewModel.setLoading(true)
    assertTrue(userViewModel.uiState.value.loading)
  }

  @Test
  fun testImageHelpers() {

    val uri = Uri.parse("content://media/external/images/media/1")
    val imageName = "test"
    val tempFile = File("tempFile.tmp")

    userViewModel.uploadImage(uri, imageName) {}
    userViewModel.updateImage(imageName) {}

    coVerify(exactly = 1) {
      mockStorage.uploadImageToFirebaseStorage(uri, Firebase.storage, imageName, any())
    }
    coVerify(exactly = 1) {
      mockStorage.getImageFromFirebaseStorage(imageName, Firebase.storage, any(), any())
    }
  }
}
