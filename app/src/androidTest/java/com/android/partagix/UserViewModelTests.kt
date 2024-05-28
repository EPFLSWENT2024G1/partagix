package com.android.partagix

import com.android.partagix.model.Database
import com.android.partagix.model.UserViewModel
import com.android.partagix.model.auth.Authentication
import com.android.partagix.model.emptyConst.emptyUser
import com.android.partagix.model.user.User
import com.google.firebase.auth.FirebaseUser
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.mockkObject
import org.junit.Before
import org.junit.Test

class UserViewModelTests {
  @RelaxedMockK lateinit var userViewModel: UserViewModel
  @RelaxedMockK lateinit var mockDatabase: Database

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

    userViewModel = UserViewModel(user = currUser, db = mockDatabase)
  }

  @Test
  fun getCommentsWorks() {
    userViewModel.getComments()

    val comments = userViewModel.uiState.value.comments
    assert(comments.first().first == otherUser)
    assert(comments.first().second == comment)
  }
}
