package com.android.partagix.model.auth

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

interface SignInResultListener {
  fun onSignInSuccess(user: FirebaseUser?)

  fun onSignInFailure(errorCode: Int)
}
/**
 * Class to handle authentication with Firebase.
 *
 * @param activity the activity to use for the sign-in flow.
 * @param signInResultListener the listener to handle the sign-in result.
 */
class Authentication(
    private val activity: ComponentActivity,
    private val signInResultListener: SignInResultListener
) {
  private var signInLauncher: ActivityResultLauncher<Intent>

  init {
    signInLauncher =
        activity.registerForActivityResult(
            FirebaseAuthUIActivityResultContract(),
        ) { res ->
          onSignInResult(res)
        }
  }

    /**
     * Checks if the user is already signed in.
     *
     * @return true if the user is already signed in, false otherwise.
     */
  fun isAlreadySignedIn(): Boolean {
    return getUser() != null
  }

    /**
     * Signs the user in using Google authentication.
     */
  fun signIn() {
    Log.w(TAG, "signIn: called")
    // Choose authentication providers
    val providers =
        arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build(),
        )

    // Create and launch sign-in intent
    val signInIntent =
        AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build()
    signInLauncher.launch(signInIntent)
  }

    /**
     * Signs the user out.
     */
    fun signOut() {
        AuthUI.getInstance().signOut(activity)
    }

  private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
    val response = result.idpResponse
    if (result.resultCode == RESULT_OK) {
      // Successfully signed in
      val user = getUser()
      signInResultListener.onSignInSuccess(user)
    } else {
      // Sign in failed. If response is null the user canceled the
      // sign-in flow using the back button. Otherwise check
      // response.getError().getErrorCode() and handle the error.
      if (response != null) {
        response.error?.let { signInResultListener.onSignInFailure(it.errorCode) }
      }
    }
  }

  companion object {
    fun getUser(): FirebaseUser? {
      return FirebaseAuth.getInstance().currentUser
    }

    private const val TAG = "Authentication"
  }
}
