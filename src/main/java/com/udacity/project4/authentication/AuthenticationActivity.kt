package com.udacity.project4.authentication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R
import com.udacity.project4.locationreminders.RemindersActivity

/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authenticationState.observe(this) {
            when (it) {
                AuthenticationState.AUTHENTICATED -> {
                    startActivity(Intent(this, RemindersActivity::class.java))
                }
                AuthenticationState.UNAUTHENTICATED -> {
                    setContentView(R.layout.activity_authentication)
                    val loginButton = findViewById<Button>(R.id.loginButton)
                    loginButton.setOnClickListener {
                        launchSignInFlow()
                    }
                }
                else ->
                    Log.e(
                        TAG, "Authentication state that doesn't require any UI change " +
                                "$authenticationState"
                    )
            }
        }
    }

    private fun launchSignInFlow() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build()
        )
        val layout = AuthMethodPickerLayout.Builder(R.layout.auth_layout)
            .setGoogleButtonId(R.id.googleButton)
            .setEmailButtonId(R.id.emailButton)
            .build()

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAuthMethodPickerLayout(layout)
                .setAvailableProviders(providers)
                .build(), SIGN_IN_RESULT_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Activity.RESULT_OK) {
            Log.i(
                TAG,
                "User ${FirebaseAuth.getInstance().currentUser?.displayName} has signed in."
            )
        } else {
            Log.i(TAG, "Sign in unsuccessful: $resultCode")
        }
    }


    companion object {
        const val TAG = "Auth"
        const val SIGN_IN_RESULT_CODE = 1001
    }
}